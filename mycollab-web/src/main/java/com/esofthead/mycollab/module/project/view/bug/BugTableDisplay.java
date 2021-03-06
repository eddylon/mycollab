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

package com.esofthead.mycollab.module.project.view.bug;

import com.esofthead.mycollab.common.TableViewField;
import com.esofthead.mycollab.common.i18n.GenericI18Enum;
import com.esofthead.mycollab.configuration.SiteConfiguration;
import com.esofthead.mycollab.eventmanager.EventBusFactory;
import com.esofthead.mycollab.module.project.*;
import com.esofthead.mycollab.module.project.events.BugEvent;
import com.esofthead.mycollab.module.project.i18n.OptionI18nEnum;
import com.esofthead.mycollab.module.project.i18n.OptionI18nEnum.BugPriority;
import com.esofthead.mycollab.module.project.i18n.OptionI18nEnum.BugResolution;
import com.esofthead.mycollab.module.project.i18n.OptionI18nEnum.BugSeverity;
import com.esofthead.mycollab.module.project.i18n.OptionI18nEnum.BugStatus;
import com.esofthead.mycollab.module.project.view.settings.component.ProjectUserLink;
import com.esofthead.mycollab.module.tracker.domain.SimpleBug;
import com.esofthead.mycollab.module.tracker.domain.criteria.BugSearchCriteria;
import com.esofthead.mycollab.module.tracker.service.BugService;
import com.esofthead.mycollab.spring.ApplicationContextUtil;
import com.esofthead.mycollab.vaadin.AppContext;
import com.esofthead.mycollab.vaadin.ui.ConfirmDialogExt;
import com.esofthead.mycollab.vaadin.ui.LabelLink;
import com.esofthead.mycollab.vaadin.ui.UIConstants;
import com.esofthead.mycollab.vaadin.ui.table.DefaultPagedBeanTable;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Resource;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import org.apache.commons.lang3.StringUtils;
import org.vaadin.dialogs.ConfirmDialog;
import org.vaadin.peter.contextmenu.ContextMenu;
import org.vaadin.peter.contextmenu.ContextMenu.ContextMenuItem;
import org.vaadin.peter.contextmenu.ContextMenu.ContextMenuItemClickEvent;
import org.vaadin.peter.contextmenu.ContextMenu.ContextMenuItemClickListener;

import java.util.List;

/**
 * 
 * @author MyCollab Ltd.
 * @since 1.0
 */
public class BugTableDisplay extends
		DefaultPagedBeanTable<BugService, BugSearchCriteria, SimpleBug>
		implements IBugCallbackStatusComp {
	private static final long serialVersionUID = 1L;

	BugTableDisplay(List<TableViewField> displayColumns) {
		this(null, displayColumns);
	}

	public BugTableDisplay(TableViewField requiredColumn,
			List<TableViewField> displayColumns) {
		this(null, requiredColumn, displayColumns);

	}

	public BugTableDisplay(String viewId, TableViewField requiredColumn,
			List<TableViewField> displayColumns) {
		super(ApplicationContextUtil.getSpringBean(BugService.class),
				SimpleBug.class, viewId, requiredColumn, displayColumns);

		this.addGeneratedColumn("id", new Table.ColumnGenerator() {
			private static final long serialVersionUID = 1L;

			@Override
			public Object generateCell(final Table source, final Object itemId,
					Object columnId) {
				final SimpleBug bug = BugTableDisplay.this
						.getBeanByIndex(itemId);
				final Button bugSettingBtn = new Button(null, FontAwesome.COG);
				bugSettingBtn.setStyleName("link");

				final ContextMenu contextMenu = new ContextMenu();
				contextMenu.setAsContextMenuOf(bugSettingBtn);

				contextMenu
						.addItemClickListener(new ContextMenuItemClickListener() {

							@Override
							public void contextMenuItemClicked(
									ContextMenuItemClickEvent event) {
								if (((ContextMenuItem) event.getSource())
										.getData() == null) {
									return;
								}

								String category = ((MenuItemData) ((ContextMenuItem) event
										.getSource()).getData()).getAction();
								String value = ((MenuItemData) ((ContextMenuItem) event
										.getSource()).getData()).getKey();
								if ("status".equals(category)) {
									if (AppContext.getMessage(
											BugStatus.Verified).equals(value)) {
										UI.getCurrent().addWindow(
												new ApproveInputWindow(
														BugTableDisplay.this,
														bug));
									} else if (AppContext.getMessage(
											BugStatus.InProgress).equals(value)) {
										bug.setStatus(BugStatus.InProgress
												.name());
										BugService bugService = ApplicationContextUtil
												.getSpringBean(BugService.class);
										bugService.updateWithSession(bug,
												AppContext.getUsername());
									} else if (AppContext.getMessage(
											BugStatus.Open).equals(value)) {
										UI.getCurrent().addWindow(
												new ReOpenWindow(
														BugTableDisplay.this,
														bug));
									} else if (AppContext.getMessage(
											BugStatus.Resolved).equals(value)) {
										UI.getCurrent().addWindow(
												new ResolvedInputWindow(
														BugTableDisplay.this,
														bug));
									}
								} else if ("severity".equals(category)) {
									bug.setSeverity(value);
									BugService bugService = ApplicationContextUtil
											.getSpringBean(BugService.class);
									bugService.updateWithSession(bug,
											AppContext.getUsername());
									refresh();
								} else if ("priority".equals(category)) {
									bug.setPriority(value);
									BugService bugService = ApplicationContextUtil
											.getSpringBean(BugService.class);
									bugService.updateWithSession(bug,
											AppContext.getUsername());
									refresh();
								} else if ("action".equals(category)) {
									if ("edit".equals(value)) {
										EventBusFactory.getInstance().post(
												new BugEvent.GotoEdit(
														BugTableDisplay.this,
														bug));
									} else if ("delete".equals(value)) {
										ConfirmDialogExt.show(
												UI.getCurrent(),
												AppContext
														.getMessage(
																GenericI18Enum.DIALOG_DELETE_TITLE,
																SiteConfiguration
																		.getSiteName()),
												AppContext
														.getMessage(GenericI18Enum.DIALOG_DELETE_SINGLE_ITEM_MESSAGE),
												AppContext
														.getMessage(GenericI18Enum.BUTTON_YES),
												AppContext
														.getMessage(GenericI18Enum.BUTTON_NO),
												new ConfirmDialog.Listener() {
													private static final long serialVersionUID = 1L;

													@Override
													public void onClose(
															ConfirmDialog dialog) {
														if (dialog
																.isConfirmed()) {
															BugService bugService = ApplicationContextUtil
																	.getSpringBean(BugService.class);
															bugService
																	.removeWithSession(
																			bug.getId(),
																			AppContext
																					.getUsername(),
																			AppContext
																					.getAccountId());
															refresh();
														}
													}
												});

									}
								}

							}
						});

				bugSettingBtn.setEnabled(CurrentProjectVariables
						.canWrite(ProjectRolePermissionCollections.BUGS));

				bugSettingBtn.addClickListener(new Button.ClickListener() {
					private static final long serialVersionUID = 1L;

					@Override
					public void buttonClick(ClickEvent event) {
						displayContextMenuItem(contextMenu, bug,
								event.getClientX(), event.getClientY());
					}
				});
				return bugSettingBtn;
			}
		});

		this.addGeneratedColumn("assignuserFullName",
				new Table.ColumnGenerator() {
					private static final long serialVersionUID = 1L;

					@Override
					public com.vaadin.ui.Component generateCell(Table source,
							final Object itemId, Object columnId) {
						final SimpleBug bug = BugTableDisplay.this
								.getBeanByIndex(itemId);
						return new ProjectUserLink(bug.getAssignuser(), bug
								.getAssignUserAvatarId(), bug
								.getAssignuserFullName());
					}
				});

		this.addGeneratedColumn("loguserFullName", new Table.ColumnGenerator() {
			private static final long serialVersionUID = 1L;

			@Override
			public com.vaadin.ui.Component generateCell(Table source,
					final Object itemId, Object columnId) {
				final SimpleBug bug = BugTableDisplay.this
						.getBeanByIndex(itemId);
				return new ProjectUserLink(bug.getLogby(), bug
						.getLoguserAvatarId(), bug.getLoguserFullName());
			}
		});

		this.addGeneratedColumn("summary", new Table.ColumnGenerator() {
			private static final long serialVersionUID = 1L;

			@Override
			public com.vaadin.ui.Component generateCell(Table source,
					final Object itemId, Object columnId) {

				final SimpleBug bug = BugTableDisplay.this
						.getBeanByIndex(itemId);

				String bugname = "[%s-%s] %s";
				bugname = String.format(bugname, CurrentProjectVariables
						.getProject().getShortname(), bug.getBugkey(), bug
						.getSummary());
				LabelLink b = new LabelLink(bugname, ProjectLinkBuilder
						.generateBugPreviewFullLink(bug.getBugkey(),
								bug.getProjectShortName()));

				if (StringUtils.isNotBlank(bug.getPriority())) {
					b.setIconLink(ProjectResources
							.getIconResourceLink12ByBugPriority(bug
									.getPriority()));
				}

				b.setDescription(ProjectTooltipGenerator.generateToolTipBug(
						AppContext.getUserLocale(), bug,
						AppContext.getSiteUrl(), AppContext.getTimezone()));

				if (bug.isCompleted()) {
					b.addStyleName(UIConstants.LINK_COMPLETED);
				} else if (bug.isOverdue()) {
					b.addStyleName(UIConstants.LINK_OVERDUE);
				}
				return b;

			}
		});

		this.addGeneratedColumn("severity", new Table.ColumnGenerator() {
			private static final long serialVersionUID = 1L;

			@Override
			public com.vaadin.ui.Component generateCell(Table source,
					final Object itemId, Object columnId) {
				final SimpleBug bug = BugTableDisplay.this
						.getBeanByIndex(itemId);

				Resource iconPriority = new ExternalResource(ProjectResources
						.getIconResourceLink12ByBugSeverity(bug.getPriority()));

				Embedded iconEmbedded = new Embedded(null, iconPriority);
				Label lbPriority = new Label(AppContext.getMessage(
						BugSeverity.class, bug.getSeverity()));
				HorizontalLayout containerField = new HorizontalLayout();
				containerField.setSpacing(true);
				containerField.addComponent(iconEmbedded);
				containerField.addComponent(lbPriority);
				containerField.setExpandRatio(lbPriority, 1.0f);
				return containerField;

			}
		});

		this.addGeneratedColumn("duedate", new Table.ColumnGenerator() {
			private static final long serialVersionUID = 1L;

			@Override
			public com.vaadin.ui.Component generateCell(Table source,
					final Object itemId, Object columnId) {
				final SimpleBug bug = BugTableDisplay.this
						.getBeanByIndex(itemId);
				return new Label(AppContext.formatDate(bug.getDuedate()));

			}
		});

		this.addGeneratedColumn("createdtime", new Table.ColumnGenerator() {
			private static final long serialVersionUID = 1L;

			@Override
			public com.vaadin.ui.Component generateCell(Table source,
					final Object itemId, Object columnId) {
				final SimpleBug bug = BugTableDisplay.this
						.getBeanByIndex(itemId);
				return new Label(
						AppContext.formatDateTime(bug.getCreatedtime()));

			}
		});

		this.addGeneratedColumn("resolution", new Table.ColumnGenerator() {
			private static final long serialVersionUID = 1L;

			@Override
			public Object generateCell(Table source, Object itemId,
					Object columnId) {
				final SimpleBug bug = BugTableDisplay.this
						.getBeanByIndex(itemId);
				return new Label(AppContext.getMessage(BugResolution.class,
						bug.getResolution()));
			}
		});
		this.setWidth("100%");
	}

	private void displayContextMenuItem(ContextMenu contextMenu, SimpleBug bug,
			int locx, int locy) {
		contextMenu.open(locx - 25, locx);
		contextMenu.removeAllItems();

		contextMenu.addItem("Edit").setData(new MenuItemData("action", "edit"));

		ContextMenuItem statusMenuItem = contextMenu.addItem("Status");
		if (BugStatus.Open.name().equals(bug.getStatus())
				|| BugStatus.ReOpened.name().equals(bug.getStatus())) {
			statusMenuItem.addItem("Start Progress").setData(
					new MenuItemData("status", AppContext
							.getMessage(BugStatus.InProgress)));
			statusMenuItem.addItem("Resolved").setData(
					new MenuItemData("status", AppContext
							.getMessage(BugStatus.Resolved)));
			statusMenuItem.addItem("Won't Fix").setData(
					new MenuItemData("status", AppContext
							.getMessage(BugStatus.Resolved)));
		} else if (BugStatus.InProgress.name().equals(bug.getStatus())) {
			statusMenuItem.addItem("Stop Progress").setData(
					new MenuItemData("status", AppContext
							.getMessage(BugStatus.Open)));
			statusMenuItem.addItem("Resolved").setData(
					new MenuItemData("status", AppContext
							.getMessage(BugStatus.Resolved)));
		} else if (BugStatus.Verified.name().equals(bug.getStatus())) {
			statusMenuItem.addItem("ReOpen").setData(
					new MenuItemData("status", AppContext
							.getMessage(BugStatus.ReOpened)));
		} else if (BugStatus.Resolved.name().equals(bug.getStatus())) {
			statusMenuItem.addItem("ReOpen").setData(
					new MenuItemData("status", AppContext
							.getMessage(BugStatus.ReOpened)));
			statusMenuItem.addItem("Approve & Close").setData(
					new MenuItemData("status", AppContext
							.getMessage(BugStatus.Verified)));
		}

		// Show bug priority
		ContextMenuItem priorityMenuItem = contextMenu.addItem("Priority");
		for (BugPriority bugPriority : OptionI18nEnum.bug_priorities) {
			ContextMenuItem prioritySubMenuItem = priorityMenuItem
					.addItem(AppContext.getMessage(bugPriority));
			prioritySubMenuItem.setData(new MenuItemData("priority", AppContext
					.getMessage(bugPriority)));
			if (bugPriority.name().equals(bug.getPriority())) {
				prioritySubMenuItem.setEnabled(false);
			}
		}

		// Show bug severity
		ContextMenuItem severityMenuItem = contextMenu.addItem("Severity");
		for (String bugSeverity : ProjectDataTypeFactory.getBugSeverityList()) {
			ContextMenuItem severitySubMenuItem = severityMenuItem
					.addItem(bugSeverity);
			severityMenuItem.setData(new MenuItemData("severity", bugSeverity));
			if (bugSeverity.equals(bug.getSeverity())) {
				severitySubMenuItem.setEnabled(false);
			}
		}

		// Add delete button
		ContextMenuItem deleteMenuItem = contextMenu.addItem("Delete");
		deleteMenuItem.setData(new MenuItemData("action", "delete"));
		deleteMenuItem.setEnabled(CurrentProjectVariables
				.canAccess(ProjectRolePermissionCollections.BUGS));
	}

	private static class MenuItemData {
		private String action;

		private String key;

		public MenuItemData(String action, String key) {
			this.action = action;
			this.key = key;
		}

		public String getAction() {
			return action;
		}

		public String getKey() {
			return key;
		}
	}

	@Override
	public void refreshBugItem() {
		this.refresh();

	}
}
