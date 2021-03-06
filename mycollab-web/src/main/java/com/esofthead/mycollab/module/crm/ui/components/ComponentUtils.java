/**
 * This file is part of mycollab-web.
 *
 * mycollab-web is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * mycollab-web is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with mycollab-web.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.esofthead.mycollab.module.crm.ui.components;

import com.esofthead.mycollab.vaadin.ui.UIConstants;
import com.vaadin.server.FontAwesome;
import org.vaadin.maddon.button.MButton;

/**
 * @author MyCollab Ltd.
 * @since 5.0.0
 */
public class ComponentUtils {
    public static MButton createCustomizeViewButton() {
        MButton customizeViewBtn = new MButton("");
        customizeViewBtn.setIcon(FontAwesome.ADJUST);
        customizeViewBtn.setDescription("Layout Options");
        customizeViewBtn.setStyleName(UIConstants.THEME_BLUE_LINK);
        customizeViewBtn.addStyleName("small-padding");
        return customizeViewBtn;
    }

    public static MButton createImportEntitiesButton() {
        MButton importBtn = new MButton("");
        importBtn.setDescription("Import");
        importBtn.setIcon(FontAwesome.CLOUD_UPLOAD);
        importBtn.setStyleName(UIConstants.THEME_BLUE_LINK);
        importBtn.addStyleName("small-padding");
        return importBtn;
    }
}
