package org.moskito.control.core;

import org.configureme.sources.ConfigurationSource;
import org.configureme.sources.ConfigurationSourceKey;
import org.configureme.sources.ConfigurationSourceListener;
import org.configureme.sources.ConfigurationSourceRegistry;
import org.moskito.control.config.ComponentConfig;
import org.moskito.control.config.MoskitoControlConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Manages applications.
 *
 * @author lrosenberg
 * @since 01.04.13 23:08
 */
public final class ComponentRepository {

	/**
	 * Map with currently configured components.
	 */
	private ConcurrentMap<String, Component> components;

	private ConcurrentMap<String, View> views;

	/**
	 * Manages components events
	 */
	private EventsDispatcher eventsDispatcher = new EventsDispatcher();

	/**
	 * Logger.
	 */
	private static Logger log = LoggerFactory.getLogger(ComponentRepository.class);

	/**
	 * Timestamp of the last application status update.
	 */
	private long lastStatusUpdaterRun;
	/**
	 * Timestamp of the last chart data update.
	 */
	private long lastChartUpdaterRun;

	/**
	 * Timestamp of the last successful application status update.
	 */
	private long lastStatusUpdaterSuccess;
	/**
	 * Timestamp of the last successful chart data update.
	 */
	private long lastChartUpdaterSuccess;

	/**
	 * Number of the status updater runs.
	 */
	private long statusUpdaterRunCount;
	/**
	 * Number of the chart updater runs.
	 */
	private long chartUpdaterRunCount;

	/**
	 * Number of the successful status updater runs.
	 */
	private long statusUpdaterSuccessCount;
	/**
	 * Number of the successful chart updater runs.
	 */
	private long chartUpdaterSuccessCount;



	/**
	 * Returns the singleton instance of the ApplicationRepository.
	 * @return instance of the ApplicationRepository
	 */
	public static final ComponentRepository getInstance(){
		return ComponentRepositoryInstanceHolder.instance;
	}

	/**
	 * Creates a new repository.
	 */
	private ComponentRepository(){
		components = new ConcurrentHashMap<>();
		views = new ConcurrentHashMap<>();

		readConfig();

        //Listen for config updates
        final ConfigurationSourceKey sourceKey = new ConfigurationSourceKey(ConfigurationSourceKey.Type.FILE, ConfigurationSourceKey.Format.JSON, "moskitocontrol");
        ConfigurationSourceRegistry.INSTANCE.addListener(sourceKey, new ConfigurationSourceListener() {
            public void configurationSourceUpdated(ConfigurationSource target) {
                log.info("Configuration file updated, reading config...");
                readConfig();
            }
        });
	}

    /**
     * Read applications configuration.
     */
	private void readConfig(){
        components.clear();
		ComponentConfig[] configuredComponents = MoskitoControlConfiguration.getConfiguration().getComponents();
		//TODO check if it is ok for configuredComponents to be NULL.
		if (configuredComponents == null)
			return;
		for (ComponentConfig cc : configuredComponents){

			Component comp = new Component(cc);

			addComponent(comp);

			//TODO add data widgets
			//app.setWidgets(ac.getDataWidgets());

			//TODO add charts
			/*
			if (ac.getCharts()!=null && ac.getCharts().length>0){
				for (ChartConfig cc : ac.getCharts()){
					Chart chart = new Chart(app, cc.getName(), cc.getLimit());

					ChartLineConfig[] lines = cc.getLines();

					for (ChartLineConfig line : lines){

						for (String componentName :
								line.getComponentsMatcher().getMatchedComponents(ac.getComponents())
								)

							chart.addLine(componentName, line.getAccumulator(), line.getCaption(componentName));

					}

					app.addChart(chart);

				}
			} */


			//TODO create views.
			View defaultView = new View("ALL");
			views.put("ALL", defaultView);
		}

		System.out.println("Past config ");
		System.out.println("components: "+components);
		System.out.println("views: "+views);
	}

	public List<View> getViews(){
		LinkedList<View> viewLinkedList = new LinkedList<>();
		viewLinkedList.addAll(views.values());
		return viewLinkedList;
	}

	public List<Component> getComponents(){
		LinkedList<Component> componentsList = new LinkedList<>();
		componentsList.addAll(components.values());
		return componentsList;
	}

	public Component getComponent(String componentName){
		return components.get(componentName);
	}

	private void addComponent(Component component){
		components.put(component.getName(), component);
	}


	public EventsDispatcher getEventsDispatcher() {
		return eventsDispatcher;
	}

	public View getView(String name) {
		return views.get(name);
	}

	/**
	 * Singleton instance holder class.
	 */
	private static class ComponentRepositoryInstanceHolder{
		/**
		 * Singleton instance.
		 */
		private static final ComponentRepository instance = new ComponentRepository();
	}

	public long getLastStatusUpdaterRun() {
		return lastStatusUpdaterRun;
	}

	public void setLastStatusUpdaterRun(long lastStatusUpdaterRun) {
		statusUpdaterRunCount++;
		this.lastStatusUpdaterRun = lastStatusUpdaterRun;
	}

	public long getLastChartUpdaterRun() {
		return lastChartUpdaterRun;
	}

	public void setLastChartUpdaterRun(long lastChartUpdaterRun) {
		chartUpdaterRunCount++;
		this.lastChartUpdaterRun = lastChartUpdaterRun;
	}

	public long getLastStatusUpdaterSuccess() {
		return lastStatusUpdaterSuccess;
	}

	public void setLastStatusUpdaterSuccess(long lastStatusUpdaterSuccess) {
		statusUpdaterSuccessCount++;
		this.lastStatusUpdaterSuccess = lastStatusUpdaterSuccess;
	}

	public long getLastChartUpdaterSuccess() {
		return lastChartUpdaterSuccess;
	}

	public void setLastChartUpdaterSuccess(long lastChartUpdaterSuccess) {
		chartUpdaterSuccessCount++;
		this.lastChartUpdaterSuccess = lastChartUpdaterSuccess;
	}

	public long getStatusUpdaterRunCount() {
		return statusUpdaterRunCount;
	}

	public long getChartUpdaterRunCount() {
		return chartUpdaterRunCount;
	}

	public long getStatusUpdaterSuccessCount() {
		return statusUpdaterSuccessCount;
	}

	public long getChartUpdaterSuccessCount() {
		return chartUpdaterSuccessCount;
	}

	/**
	 * Returns the worst status of an application component, which is the worst status of the application.
	 * @return worst status of this application
	 */
	public HealthColor getWorstHealthStatus() {
		HealthColor ret = HealthColor.GREEN;
		for (Component c : getComponents()){ //TODO revisit - iterate directly over hashmap
			if (c.getHealthColor().isWorse(ret))
				ret = c.getHealthColor();
		}
		return ret;
	}

}
