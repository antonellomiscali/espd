/*
 *
 * Copyright 2016 EUROPEAN COMMISSION
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they
 * will be approved by the European Commission - subsequent
 * versions of the EUPL (the "Licence");
 *
 * You may not use this work except in compliance with the Licence.
 *
 * You may obtain a copy of the Licence at:
 *
 * https://joinup.ec.europa.eu/community/eupl/og_page/eupl
 *
 * Unless required by applicable law or agreed to in
 * writing, software distributed under the Licence is
 * distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied.
 * See the Licence for the specific language governing
 * permissions and limitations under the Licence.
 *
 */

package eu.europa.ec.grow.espd.xml.request.selection

import eu.europa.ec.grow.espd.domain.enums.criteria.SelectionCriterion
import eu.europa.ec.grow.espd.domain.EconomicFinancialStandingCriterion
import eu.europa.ec.grow.espd.domain.EspdDocument
import eu.europa.ec.grow.espd.xml.base.AbstractSelectionCriteriaFixture
/**
 * Created by ratoico on 12/9/15 at 1:48 PM.
 */
class SpecificYearlyTurnoverRequestTest extends AbstractSelectionCriteriaFixture {

    def "08. should contain the 'Specific yearly turnover' criterion"() {
        given:
        def espd = new EspdDocument(specificYearlyTurnover: new EconomicFinancialStandingCriterion(exists: true))

        when:
        def request = parseRequestXml(espd)
        def idx = getRequestCriterionIndex(SelectionCriterion.SPECIFIC_YEARLY_TURNOVER)

        then: "CriterionID element"
        checkCriterionId(request, idx, "074f6031-55f9-4e99-b9a4-c4363e8bc315")

        then: "CriterionTypeCode element"
        checkCriterionTypeCode(request, idx, "SELECTION.ECONOMIC_FINANCIAL_STANDING")

        then: "CriterionName element"
        request.Criterion[idx].Name.text() == "Specific yearly turnover"

        then: "CriterionDescription element"
        request.Criterion[idx].Description.text() == "Its specific yearly turnover in the business area covered by the contract for the number of financial years required in the relevant notice, the procurement documents or the ESPD is as follows:"

        then: "CriterionLegislationReference element"
        checkLegislationReference(request, idx, "58(3)")

        then: "check all the sub groups"
        request.Criterion[idx].RequirementGroup.size() == 2

        then: "main sub group"
        request.Criterion[idx].RequirementGroup[0].ID.text() == "ee1fdbab-f54e-4579-bcb8-060fe45178e9"
        request.Criterion[idx].RequirementGroup[0].RequirementGroup.size() == 5
        request.Criterion[idx].RequirementGroup[0].Requirement.size() == 1

        then: "main sub group requirements"
        def r1_0 = request.Criterion[idx].RequirementGroup[0].Requirement[0]
        checkRequirement(r1_0, "15335c12-ad77-4728-b5ad-3c06a60d65a4", "Your answer?", "INDICATOR")

        then: "check year amount currency subgroups"
        checkYearAmountCurrencyGroup1(request.Criterion[idx].RequirementGroup[0].RequirementGroup[0])
        checkYearAmountCurrencyGroup2(request.Criterion[idx].RequirementGroup[0].RequirementGroup[1])
        checkYearAmountCurrencyGroup3(request.Criterion[idx].RequirementGroup[0].RequirementGroup[2])
        checkYearAmountCurrencyGroup4(request.Criterion[idx].RequirementGroup[0].RequirementGroup[3])
        checkYearAmountCurrencyGroup5(request.Criterion[idx].RequirementGroup[0].RequirementGroup[4])

        then: "info available electronically sub group"
        checkInfoAvailableElectronicallyRequirementGroup(request.Criterion[idx].RequirementGroup[1])
    }

}