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
package com.esofthead.mycollab.module.project.view.user;

import com.esofthead.mycollab.common.i18n.GenericI18Enum;
import com.esofthead.mycollab.configuration.StorageManager;
import com.esofthead.mycollab.core.arguments.DateSearchField;
import com.esofthead.mycollab.core.arguments.SearchField;
import com.esofthead.mycollab.core.arguments.SetSearchField;
import com.esofthead.mycollab.core.arguments.StringSearchField;
import com.esofthead.mycollab.core.utils.DateTimeUtils;
import com.esofthead.mycollab.html.DivLessFormatter;
import com.esofthead.mycollab.module.project.CurrentProjectVariables;
import com.esofthead.mycollab.module.project.ProjectLinkBuilder;
import com.esofthead.mycollab.module.project.ProjectTypeConstants;
import com.esofthead.mycollab.module.project.domain.ProjectGenericTask;
import com.esofthead.mycollab.module.project.domain.criteria.ProjectGenericTaskSearchCriteria;
import com.esofthead.mycollab.module.project.i18n.ProjectCommonI18nEnum;
import com.esofthead.mycollab.module.project.i18n.TaskI18nEnum;
import com.esofthead.mycollab.module.project.service.ProjectGenericTaskService;
import com.esofthead.mycollab.module.project.ui.ProjectAssetsManager;
import com.esofthead.mycollab.spring.ApplicationContextUtil;
import com.esofthead.mycollab.utils.TooltipHelper;
import com.esofthead.mycollab.vaadin.AppContext;
import com.esofthead.mycollab.vaadin.ui.DefaultBeanPagedList;
import com.hp.gagawa.java.elements.A;
import com.hp.gagawa.java.elements.Div;
import com.hp.gagawa.java.elements.Img;
import com.hp.gagawa.java.elements.Text;
import com.vaadin.data.Property;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.*;
import org.vaadin.maddon.layouts.MHorizontalLayout;
import org.vaadin.maddon.layouts.MVerticalLayout;

import java.util.Date;
import java.util.UUID;

/**
 * @author MyCollab Ltd.
 * @since 4.1
 */
public class ProjectAssignmentsWidget extends MVerticalLayout {

    private static final long serialVersionUID = 1L;

    private  ProjectGenericTaskSearchCriteria searchCriteria;

    private Label titleLbl;
    private final DefaultBeanPagedList<ProjectGenericTaskService, ProjectGenericTaskSearchCriteria, ProjectGenericTask> taskList;

    public ProjectAssignmentsWidget() {
        withSpacing(false).withMargin(false);
        titleLbl = new Label(AppContext.getMessage(ProjectCommonI18nEnum.WIDGET_OPEN_ASSIGNMENTS_TITLE, 0));

        final CheckBox overdueSelection = new CheckBox("Overdue");
        overdueSelection.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent valueChangeEvent) {
                boolean isOverdueOption = overdueSelection.getValue();
                if (isOverdueOption) {
                    searchCriteria.setDueDate(new DateSearchField(DateSearchField.AND,
                            DateTimeUtils.getCurrentDateWithoutMS()));
                } else {
                    searchCriteria.setDueDate(null);
                }
                updateSearchResult();
            }
        });

        final CheckBox myItemsSelection = new CheckBox("My Items");
        myItemsSelection.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent valueChangeEvent) {
                boolean isMyItemsOption = myItemsSelection.getValue();
                if (isMyItemsOption) {
                    searchCriteria.setAssignUser(new StringSearchField(AppContext.getUsername()));
                } else {
                    searchCriteria.setAssignUser(null);
                }
                updateSearchResult();
            }
        });

        MHorizontalLayout header = new MHorizontalLayout().withSpacing(true).withMargin(new MarginInfo(false, true,
                false, true)).withHeight("34px").with
                (titleLbl, overdueSelection, myItemsSelection).withAlign
                (titleLbl, Alignment.MIDDLE_LEFT).withAlign(overdueSelection, Alignment.MIDDLE_RIGHT).withAlign
                (myItemsSelection, Alignment.MIDDLE_RIGHT).expand(titleLbl);
        header.addStyleName("panel-header");

        taskList = new DefaultBeanPagedList<>(
                ApplicationContextUtil
                        .getSpringBean(ProjectGenericTaskService.class),
                new TaskRowDisplayHandler(), 10);
        this.with(header, taskList);
    }

    public void showOpenAssignments() {
        searchCriteria = new ProjectGenericTaskSearchCriteria();
        searchCriteria.setIsOpenned(new SearchField());
        searchCriteria.setProjectIds(new SetSearchField<>(
                CurrentProjectVariables.getProjectId()));
        updateSearchResult();
    }

    private void updateSearchResult() {
        taskList.setSearchCriteria(searchCriteria);
        titleLbl.setValue(AppContext.getMessage(ProjectCommonI18nEnum.WIDGET_OPEN_ASSIGNMENTS_TITLE, taskList.getTotalCount()));
    }

    public static class TaskRowDisplayHandler implements
            DefaultBeanPagedList.RowDisplayHandler<ProjectGenericTask> {

        @Override
        public Component generateRow(final ProjectGenericTask genericTask,
                                     final int rowIndex) {
            final CssLayout layout = new CssLayout();
            layout.setWidth("100%");
            layout.setStyleName("list-row");

            if ((rowIndex + 1) % 2 != 0) {
                layout.addStyleName("odd");
            }

            Div itemDiv = buildItemValue(genericTask);

            Label taskLbl = new Label(itemDiv.write(), ContentMode.HTML);
            if (genericTask.isOverdue()) {
              taskLbl.addStyleName("overdue");
            }

            layout.addComponent(taskLbl);

            Div footerDiv = new Div().setCSSClass("activity-date");

            Date dueDate = genericTask.getDueDate();
            if (dueDate != null) {
                footerDiv.appendChild(new Text(AppContext.getMessage(
                        TaskI18nEnum.OPT_DUE_DATE,
                        DateTimeUtils.getPrettyDateValue(dueDate,
                                AppContext.getUserLocale()))));
            } else {
                footerDiv.appendChild(new Text(AppContext.getMessage(
                        TaskI18nEnum.OPT_DUE_DATE, "Undefined")));
            }


            if (genericTask.getAssignUser() != null) {
               footerDiv.appendChild(buildAssigneeValue(genericTask));
            }

            layout.addComponent(new Label(footerDiv.write(), ContentMode.HTML));
            return layout;
        }

        private Div buildItemValue(ProjectGenericTask task) {
            String uid = UUID.randomUUID().toString();
            Div div = new DivLessFormatter();
            Text image = new Text(ProjectAssetsManager.getAsset(task.getType()).getHtml());
            A itemLink = new A();
            itemLink.setId("tag" + uid);
            if (ProjectTypeConstants.TASK.equals(task.getType())
                    || ProjectTypeConstants.BUG.equals(task.getType())) {
                itemLink.setHref(ProjectLinkBuilder.generateProjectItemLink(
                        task.getProjectShortName(),
                        task.getProjectId(), task.getType(),
                        task.getExtraTypeId() + ""));
            } else {
                itemLink.setHref(ProjectLinkBuilder.generateProjectItemLink(
                        task.getProjectShortName(),
                        task.getProjectId(), task.getType(),
                        task.getTypeId() + ""));
            }


            String arg17 = "'" + uid + "'";
            String arg18 = "'" + task.getType() + "'";
            String arg19 = "'" + task.getTypeId() + "'";
            String arg20 = "'" + AppContext.getSiteUrl() + "tooltip/'";
            String arg21 = "'" + AppContext.getAccountId() + "'";
            String arg22 = "'" + AppContext.getSiteUrl() + "'";
            String arg23 = AppContext.getSession().getTimezone();
            String arg24 = "'" + AppContext.getUserLocale().toString() + "'";

            String mouseOverFunc = String.format(
                    "return overIt(%s,%s,%s,%s,%s,%s,%s,%s);", arg17, arg18, arg19,
                    arg20, arg21, arg22, arg23, arg24);
            itemLink.setAttribute("onmouseover", mouseOverFunc);
            itemLink.appendText(task.getName());

            div.appendChild(image, DivLessFormatter.EMPTY_SPACE(), itemLink, DivLessFormatter.EMPTY_SPACE(),
                    TooltipHelper.buildDivTooltipEnable(uid));
            return div;
        }

        private Div buildAssigneeValue(ProjectGenericTask task) {
            String uid = UUID.randomUUID().toString();
            Div div = new DivLessFormatter();
            Img userAvatar = new Img("", StorageManager.getAvatarLink(task.getAssignUserAvatarId(), 16));
            A userLink = new A();
            userLink.setId("tag" + uid);
            userLink.setHref(ProjectLinkBuilder.generateProjectMemberFullLink(
                    task.getProjectId(),
                    task.getAssignUser()));

            userLink.setAttribute("onmouseover", TooltipHelper.buildUserHtmlTooltip(uid,  task.getAssignUser()));
            userLink.appendText(task.getAssignUserFullName());

            String assigneeTxt = AppContext.getMessage(GenericI18Enum.FORM_ASSIGNEE) + ": ";

            div.appendChild(DivLessFormatter.EMPTY_SPACE(), DivLessFormatter.EMPTY_SPACE(), DivLessFormatter
                            .EMPTY_SPACE(), DivLessFormatter.EMPTY_SPACE(), new Text(assigneeTxt),
                    userAvatar, DivLessFormatter
                            .EMPTY_SPACE(), userLink,
                    DivLessFormatter.EMPTY_SPACE(),
                    TooltipHelper.buildDivTooltipEnable(uid));

            return div;
        }
    }

}