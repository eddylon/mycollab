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

package com.esofthead.mycollab.module.crm.view;

import com.esofthead.mycollab.core.arguments.NumberSearchField;
import com.esofthead.mycollab.module.crm.domain.criteria.OpportunitySearchCriteria;
import com.esofthead.mycollab.module.crm.view.opportunity.IOpportunityLeadSourceDashboard;
import com.esofthead.mycollab.module.crm.view.opportunity.IOpportunitySalesStageDashboard;
import com.esofthead.mycollab.vaadin.AppContext;
import com.esofthead.mycollab.vaadin.mvp.ViewManager;
import com.esofthead.mycollab.vaadin.ui.Depot;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.VerticalLayout;
import org.vaadin.hene.popupbutton.PopupButton;

/**
 * 
 * @author MyCollab Ltd.
 * @since 1.0
 */
public class SalesDashboardView extends Depot {
	private static final long serialVersionUID = 1L;
	private final String[] reportDashboard = { "OpportunitySalesStage",
			"OpportunityLeadSource" };
	private int currentReportIndex = 0;

	public SalesDashboardView() {
		super("Sales Dashboard", null, new VerticalLayout(), "100%", "200px");
		this.bodyContent.setSizeFull();
		this.initUI();
		this.setHeaderColor(true);
		this.setContentBorder(true);
	}

	public void displayReport() {
		final String reportName = this.reportDashboard[this.currentReportIndex];

		final VerticalLayout bodyContent = (VerticalLayout) this.bodyContent;
		bodyContent.removeAllComponents();
		bodyContent.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);

		if ("OpportunitySalesStage".equals(reportName)) {
			this.setTitle("Opportunity Sales Stage");
			final IOpportunitySalesStageDashboard salesStageDashboard = ViewManager
					.getCacheComponent(IOpportunitySalesStageDashboard.class);
			bodyContent.addComponent(salesStageDashboard);

			final OpportunitySearchCriteria criteria = new OpportunitySearchCriteria();
			criteria.setSaccountid(new NumberSearchField(AppContext
					.getAccountId()));
			salesStageDashboard.setSearchCriteria(criteria);
		} else if ("OpportunityLeadSource".equals(reportName)) {
			this.setTitle("Opportunity Lead Source");
			final IOpportunityLeadSourceDashboard leadSourceDashboard = ViewManager
					.getCacheComponent(IOpportunityLeadSourceDashboard.class);
			bodyContent.addComponent(leadSourceDashboard);

			final OpportunitySearchCriteria criteria = new OpportunitySearchCriteria();
			criteria.setSaccountid(new NumberSearchField(AppContext
					.getAccountId()));
			leadSourceDashboard.setSearchCriteria(criteria);
		}
	}

	private void initUI() {
		final PopupButton saleChartPopup = new PopupButton("");
		saleChartPopup.addStyleName("popuplistindicator");

		final VerticalLayout filterBtnLayout = new VerticalLayout();
		filterBtnLayout.setMargin(true);
		filterBtnLayout.setSpacing(true);
		filterBtnLayout.setWidth("200px");

		final Button btnOpportunitySales = new Button(
				"Opportunity Sales Stage", new Button.ClickListener() {
					private static final long serialVersionUID = 1L;

					@Override
					public void buttonClick(final ClickEvent event) {
						saleChartPopup.setPopupVisible(false);
						SalesDashboardView.this.currentReportIndex = 0;
						SalesDashboardView.this.displayReport();
					}
				});
		btnOpportunitySales.setStyleName("link");
		filterBtnLayout.addComponent(btnOpportunitySales);

		final Button btnOpportunityLead = new Button("Opportunity Lead Source",
				new Button.ClickListener() {
					private static final long serialVersionUID = 1L;

					@Override
					public void buttonClick(final ClickEvent event) {
						saleChartPopup.setPopupVisible(false);
						SalesDashboardView.this.currentReportIndex = 1;
						SalesDashboardView.this.displayReport();
					}
				});
		btnOpportunityLead.setStyleName("link");
		filterBtnLayout.addComponent(btnOpportunityLead);

		this.displayReport();

		saleChartPopup.setContent(filterBtnLayout);
		this.addHeaderElement(saleChartPopup);
	}
}
