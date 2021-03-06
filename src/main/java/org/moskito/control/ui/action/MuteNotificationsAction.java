package org.moskito.control.ui.action;

import net.anotheria.maf.action.ActionCommand;
import net.anotheria.maf.action.ActionMapping;
import net.anotheria.maf.bean.FormBean;
import net.anotheria.util.TimeUnit;
import org.moskito.control.config.MoskitoControlConfiguration;
import org.moskito.control.core.ComponentRepository;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * The action for muting status change notification.
 *
 * @author vkazhdan
 */
public class MuteNotificationsAction extends BaseMoSKitoControlAction {
    @Override
    public ActionCommand execute(ActionMapping mapping, FormBean formBean, HttpServletRequest req, HttpServletResponse res) {
        final long delay = TimeUnit.MINUTE.getMillis(MoskitoControlConfiguration.getConfiguration().getNotificationsMutingTime());
        ComponentRepository.getInstance().getEventsDispatcher().mute(delay);
        return mapping.redirect();
    }
}
