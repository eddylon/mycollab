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
package com.esofthead.mycollab.module.crm.view.account;

import com.esofthead.mycollab.common.i18n.GenericI18Enum;
import com.esofthead.mycollab.configuration.SiteConfiguration;
import com.esofthead.mycollab.core.arguments.NumberSearchField;
import com.esofthead.mycollab.core.arguments.SearchField;
import com.esofthead.mycollab.module.crm.CrmDataTypeFactory;
import com.esofthead.mycollab.module.crm.CrmLinkGenerator;
import com.esofthead.mycollab.module.crm.CrmTypeConstants;
import com.esofthead.mycollab.module.crm.domain.Account;
import com.esofthead.mycollab.module.crm.domain.SimpleCase;
import com.esofthead.mycollab.module.crm.domain.criteria.CaseSearchCriteria;
import com.esofthead.mycollab.module.crm.i18n.CaseI18nEnum;
import com.esofthead.mycollab.module.crm.service.CaseService;
import com.esofthead.mycollab.module.crm.ui.CrmAssetsManager;
import com.esofthead.mycollab.module.crm.ui.components.RelatedListComp2;
import com.esofthead.mycollab.module.user.AccountLinkGenerator;
import com.esofthead.mycollab.security.RolePermissionCollections;
import com.esofthead.mycollab.spring.ApplicationContextUtil;
import com.esofthead.mycollab.vaadin.AppContext;
import com.esofthead.mycollab.vaadin.ui.AbstractBeanBlockList;
import com.esofthead.mycollab.vaadin.ui.ConfirmDialogExt;
import com.esofthead.mycollab.vaadin.ui.FontIconLabel;
import com.esofthead.mycollab.vaadin.ui.UIConstants;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.*;
import org.vaadin.dialogs.ConfirmDialog;
import org.vaadin.maddon.button.MButton;

import java.util.HashMap;
import java.util.Map;

/**
 * @author MyCollab Ltd.
 * @since 4.0
 */
public class AccountCaseListComp extends
        RelatedListComp2<CaseService, CaseSearchCriteria, SimpleCase> {
    private static final long serialVersionUID = -8763667647686473453L;
    private Account account;

    public static Map<String, String> colorsMap = new HashMap<>();

    static {
        for (int i = 0; i < CrmDataTypeFactory.getCasesStatusList().length; i++) {
            String roleKeyName = CrmDataTypeFactory.getCasesStatusList()[i];
            if (!colorsMap.containsKey(roleKeyName)) {
                colorsMap.put(roleKeyName,
                        AbstractBeanBlockList.getColorStyleNameList()[i]);
            }
        }
    }

    public AccountCaseListComp() {
        super(ApplicationContextUtil.getSpringBean(CaseService.class), 20);
        this.setBlockDisplayHandler(new AccountCaseBlockDisplay());
    }

    @Override
    protected Component generateTopControls() {
        HorizontalLayout controlsBtnWrap = new HorizontalLayout();
        controlsBtnWrap.setWidth("100%");

        HorizontalLayout notesWrap = new HorizontalLayout();
        notesWrap.setWidth("100%");
        notesWrap.setSpacing(true);
        Label noteLbl = new Label("Note: ");
        noteLbl.setSizeUndefined();
        noteLbl.setStyleName("list-note-lbl");
        notesWrap.addComponent(noteLbl);

        CssLayout noteBlock = new CssLayout();
        noteBlock.setWidth("100%");
        noteBlock.setStyleName("list-note-block");
        for (int i = 0; i < CrmDataTypeFactory.getCasesStatusList().length; i++) {
            Label note = new Label(CrmDataTypeFactory.getCasesStatusList()[i]);
            note.setStyleName("note-label");
            note.addStyleName(colorsMap.get(CrmDataTypeFactory
                    .getCasesStatusList()[i]));
            note.setSizeUndefined();

            noteBlock.addComponent(note);
        }
        notesWrap.addComponent(noteBlock);
        notesWrap.setExpandRatio(noteBlock, 1.0f);

        controlsBtnWrap.addComponent(notesWrap);

        controlsBtnWrap.setWidth("100%");
        final Button createBtn = new Button();
        createBtn.setSizeUndefined();
        createBtn.setEnabled(AppContext
                .canWrite(RolePermissionCollections.CRM_CASE));
        createBtn.addStyleName(UIConstants.THEME_GREEN_LINK);
        createBtn.setCaption(AppContext
                .getMessage(CaseI18nEnum.BUTTON_NEW_CASE));
        createBtn.setIcon(FontAwesome.PLUS);
        createBtn.addClickListener(new Button.ClickListener() {
            private static final long serialVersionUID = -8725970955325733072L;

            @Override
            public void buttonClick(final Button.ClickEvent event) {
                fireNewRelatedItem("");
            }
        });

        controlsBtnWrap.addComponent(createBtn);
        controlsBtnWrap
                .setComponentAlignment(createBtn, Alignment.MIDDLE_RIGHT);
        return controlsBtnWrap;
    }

    public void displayCases(final Account account) {
        this.account = account;
        loadCases();
    }

    private void loadCases() {
        final CaseSearchCriteria criteria = new CaseSearchCriteria();
        criteria.setSaccountid(new NumberSearchField(SearchField.AND,
                AppContext.getAccountId()));
        criteria.setAccountId(new NumberSearchField(SearchField.AND, account
                .getId()));
        setSearchCriteria(criteria);
    }

    @Override
    public void refresh() {
        loadCases();
    }

    public class AccountCaseBlockDisplay implements
            BlockDisplayHandler<SimpleCase> {

        @Override
        public Component generateBlock(final SimpleCase oneCase, int blockIndex) {
            CssLayout beanBlock = new CssLayout();
            beanBlock.addStyleName("bean-block");
            beanBlock.setWidth("350px");

            VerticalLayout blockContent = new VerticalLayout();
            HorizontalLayout blockTop = new HorizontalLayout();
            blockTop.setSpacing(true);
            CssLayout iconWrap = new CssLayout();
            iconWrap.setStyleName("icon-wrap");
            FontIconLabel caseIcon = new FontIconLabel(CrmAssetsManager.getAsset(CrmTypeConstants.CASE));
            iconWrap.addComponent(caseIcon);
            blockTop.addComponent(iconWrap);

            VerticalLayout caseInfo = new VerticalLayout();
            caseInfo.setSpacing(true);

            MButton deleteBtn = new MButton(FontAwesome.TRASH_O);
            deleteBtn.addClickListener(new Button.ClickListener() {
                @Override
                public void buttonClick(Button.ClickEvent clickEvent) {
                    ConfirmDialogExt.show(
                            UI.getCurrent(),
                            AppContext.getMessage(
                                    GenericI18Enum.DIALOG_DELETE_TITLE,
                                    SiteConfiguration.getSiteName()),
                            AppContext
                                    .getMessage(GenericI18Enum.DIALOG_DELETE_SINGLE_ITEM_MESSAGE),
                            AppContext
                                    .getMessage(GenericI18Enum.BUTTON_YES),
                            AppContext
                                    .getMessage(GenericI18Enum.BUTTON_NO),
                            new ConfirmDialog.Listener() {
                                private static final long serialVersionUID = 1L;

                                @Override
                                public void onClose(ConfirmDialog dialog) {
                                    if (dialog.isConfirmed()) {
                                        final CaseService caseService = ApplicationContextUtil
                                                .getSpringBean(CaseService.class);
                                        caseService.removeWithSession(
                                                oneCase.getId(),
                                                AppContext.getUsername(),
                                                AppContext.getAccountId());
                                        AccountCaseListComp.this.refresh();
                                    }
                                }
                            });
                }
            });
            deleteBtn.addStyleName(UIConstants.BUTTON_ICON_ONLY);

            blockContent.addComponent(deleteBtn);
            blockContent.setComponentAlignment(deleteBtn, Alignment.TOP_RIGHT);

            Label caseSubject = new Label("Subject: <a href='"
                    + SiteConfiguration.getSiteUrl(AppContext.getSession()
                    .getSubdomain())
                    + CrmLinkGenerator.generateCrmItemLink(
                    CrmTypeConstants.CASE, oneCase.getId()) + "'>"
                    + oneCase.getSubject() + "</a>", ContentMode.HTML);

            caseInfo.addComponent(caseSubject);

            Label casePriority = new Label("Priority: "
                    + (oneCase.getPriority() != null ? oneCase.getPriority()
                    : ""));
            caseInfo.addComponent(casePriority);

            Label caseStatus = new Label("Status: "
                    + (oneCase.getStatus() != null ? oneCase.getStatus() : ""));
            caseInfo.addComponent(caseStatus);

            if (oneCase.getStatus() != null) {
                beanBlock.addStyleName(colorsMap.get(oneCase.getStatus()));
            }

            Label caseAssignUser = new Label("Assigned User: "
                    + (oneCase.getAssignuser() != null ? "<a href='"
                    + AccountLinkGenerator.generatePreviewFullUserLink(
                    SiteConfiguration.getSiteUrl(AppContext
                            .getSession().getSubdomain()),
                    oneCase.getAssignuser()) + "'>"
                    + oneCase.getAssignUserFullName() + "</a>" : ""),
                    ContentMode.HTML);
            caseInfo.addComponent(caseAssignUser);

            Label caseCreatedTime = new Label("Created Time: "
                    + AppContext.formatDate(oneCase.getCreatedtime()));
            caseInfo.addComponent(caseCreatedTime);

            blockTop.addComponent(caseInfo);
            blockTop.setExpandRatio(caseInfo, 1.0f);
            blockTop.setWidth("100%");
            blockContent.addComponent(blockTop);

            blockContent.setWidth("100%");

            beanBlock.addComponent(blockContent);
            return beanBlock;
        }

    }

}
