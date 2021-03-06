package org.moskito.control.config;

import com.google.gson.annotations.SerializedName;
import org.configureme.annotations.ConfigureMe;
import org.moskito.control.connectors.ConnectorType;

/**
 * Represents a single configured connector.
 *
 * @author lrosenberg
 * @since 28.05.13 20:59
 */
@ConfigureMe (allfields = true)
public class ConnectorConfig {
	/**
	 * Type of the connector. Used by the factory to create an instance.
	 */
	@SerializedName("type")
	private ConnectorType type;

	/**
	 * Clazzname of the connector implementation. This class will be instantiated by the factory.
	 */
	@SerializedName("className")
	private String className;

	public ConnectorType getType() {
		return type;
	}

	public void setType(ConnectorType type) {
		this.type = type;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	@Override public String toString(){
		return getType()+": "+getClassName();
	}
}
