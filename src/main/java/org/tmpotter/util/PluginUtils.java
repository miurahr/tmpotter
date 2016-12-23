/* *************************************************************************
 *
 *  TMPotter - Bi-text Aligner/TMX Editor
 *
 *  Copyright (C) 2016 Hiroshi Miura
 *
 *  This file is part of TMPotter.
 *
 *  TMPotter is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  TMPotter is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with TMPotter.  If not, see http://www.gnu.org/licenses/.
 *
 * *************************************************************************/

package org.tmpotter.util;

import org.tmpotter.filters.IFilter;
import org.tmpotter.ui.wizard.IImportWizardPanel;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

/**
 * Plugin management utility.
 * @author Hiroshi Miura
 */
public class PluginUtils {

    /**
     * Private constructor to disallow creation.
     */
    private PluginUtils() {
    }

    public static List<IFilter> getFilters() {
        return filterClasses;
    }

    public static List<IImportWizardPanel> getWizards() {
        return wizardClasses;
    }

    static List<IFilter> filterClasses = collectAllImportFilterPlugins();
    static List<IImportWizardPanel> wizardClasses = collectAllPanelPlugins();

    private static List<IImportWizardPanel> collectAllPanelPlugins() {
        List<IImportWizardPanel> list = new ArrayList<>();
        ServiceLoader<IImportWizardPanel> loader = ServiceLoader.load(IImportWizardPanel.class,
                Thread.currentThread().getContextClassLoader());
        for (IImportWizardPanel panel : loader) {
            list.add(panel);
        }
        return list;
    }

    private static List<IFilter> collectAllImportFilterPlugins() {
        List<IFilter> list = new ArrayList<>();
        ServiceLoader<IFilter> loader = ServiceLoader.load(IFilter.class,
                Thread.currentThread().getContextClassLoader());
        for (IFilter filter : loader) {
            list.add(filter);
        }
        return list;
    }

}
