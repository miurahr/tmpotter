/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tmpotter.util;

import org.tmpotter.filters.IFilter;
import org.tmpotter.ui.wizard.IImportWizardPanel;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

/**
 *
 * @author Hiroshi Miura
 */
public class PluginUtils {

  /** Private constructor to disallow creation */
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
        ServiceLoader<IImportWizardPanel> loader = ServiceLoader.load(IImportWizardPanel.class, Thread.currentThread().getContextClassLoader());
        for (IImportWizardPanel panel: loader) {
            list.add(panel);
        }
        return list;
  }

  private static List<IFilter> collectAllImportFilterPlugins() {
      List<IFilter> list = new ArrayList<>();
      ServiceLoader<IFilter> loader = ServiceLoader.load(IFilter.class, Thread.currentThread().getContextClassLoader());
      for (IFilter filter: loader) {
          list.add(filter);
      }
      return list;
  }

}
