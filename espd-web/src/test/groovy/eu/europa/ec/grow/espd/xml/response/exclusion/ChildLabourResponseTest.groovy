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

package eu.europa.ec.grow.espd.xml.response.exclusion

import eu.europa.ec.grow.espd.domain.enums.criteria.ExclusionCriterion
import eu.europa.ec.grow.espd.domain.AvailableElectronically
import eu.europa.ec.grow.espd.domain.SelfCleaning
import eu.europa.ec.grow.espd.xml.LocalDateAdapter
import eu.europa.ec.grow.espd.xml.base.AbstractExclusionCriteriaFixture
import eu.europa.ec.grow.espd.domain.CriminalConvictionsCriterion
import eu.europa.ec.grow.espd.domain.EspdDocument
import org.joda.time.LocalDate

/**
 * Created by ratoico on 12/9/15 at 11:58 AM.
 */
class ChildLabourResponseTest extends AbstractExclusionCriteriaFixture {

    def "06. should contain the 'Child labour and other forms of trafficking in human beings' criterion"() {
        given:
        def espd = new EspdDocument(childLabour: new CriminalConvictionsCriterion(exists: true))

        when:
        def response = parseResponseXml(espd)
        def idx = getResponseCriterionIndex(ExclusionCriterion.CHILD_LABOUR)

        then: "CriterionID element"
        checkCriterionId(response, idx, "d789d01a-fe03-4ccd-9898-73f9cfa080d1")

        then: "CriterionTypeCode element"
        checkCriterionTypeCode(response, idx, "CRITERION.EXCLUSION.CONVICTIONS.CHILD_LABOUR-HUMAN_TRAFFICKING")

        then: "CriterionName element"
        response.Criterion[idx].Name.text() == "Child labour and other forms of trafficking in human beings"

        then: "CriterionDescription element"
        response.Criterion[idx].Description.text() == "Has the economic operator itself or any person who is a member of its administrative, management or supervisory body or has powers of representation, decision or control therein been the subject of a conviction by final judgment for child labour and other forms of trafficking in human beings, by a conviction rendered at the most five years ago or in which an exclusion period set out directly in the conviction continues to be applicable? As defined in Article 2 of Directive 2011/36/EU of the European Parliament and of the Council of 5 April 2011 on preventing and combating trafficking in human beings and protecting its victims, and replacing Council Framework Decision 2002/629/JHA (OJ L 101, 15.4.2011, p. 1)."

        then: "CriterionLegislationReference element"
        checkLegislationReference(response, idx, "57(1)")


        then: "check all the sub groups"
        response.Criterion[idx].RequirementGroup.size() == 2

        then: "main sub group"
        response.Criterion[idx].RequirementGroup[0].ID.text() == "7c637c0c-7703-4389-ba52-02997a055bd7"
        response.Criterion[idx].RequirementGroup[0].RequirementGroup.size() == 1
        response.Criterion[idx].RequirementGroup[0].Requirement.size() == 1

        then: "check the self-cleaning sub group"
        checkSelfCleaningRequirementGroup(response.Criterion[idx].RequirementGroup[0].RequirementGroup[0].RequirementGroup[0])

        then: "info available electronically sub group"
        checkInfoAvailableElectronicallyRequirementGroup(response.Criterion[idx].RequirementGroup[1])
    }

    def "check the 'Your answer' requirement response"() {
        given:
        def espd = new EspdDocument(childLabour: new CriminalConvictionsCriterion(exists: true, answer: null))

        when:
        def response = parseResponseXml(espd)
        def idx = getResponseCriterionIndex(ExclusionCriterion.CHILD_LABOUR)

        then:
        def subGroup = response.Criterion[idx].RequirementGroup[0]

        def req = subGroup.Requirement[0]
        checkRequirement(req, "974c8196-9d1c-419c-9ca9-45bb9f5fd59a", "Your answer?", "INDICATOR")
        req.Response.size() == 1
        req.Response[0].Indicator.text() == "false"
    }

    def "check the 'Date of conviction' requirement response"() {
        given:
        def now = new Date()
        def espd = new EspdDocument(childLabour: new CriminalConvictionsCriterion(exists: true, dateOfConviction: now))

        when:
        def response = parseResponseXml(espd)
        def idx = getResponseCriterionIndex(ExclusionCriterion.CHILD_LABOUR)

        then:
        def subGroup = response.Criterion[idx].RequirementGroup[0].RequirementGroup[0]

        def req = subGroup.Requirement[0]
        checkRequirement(req, "ecf40999-7b64-4e10-b960-7f8ff8674cf6", "Date of conviction", "DATE")
        req.Response.size() == 1
        req.Response[0].Date.text() == LocalDateAdapter.marshal(new LocalDate(now.time))
    }

    def "check the 'Reason' requirement response"() {
        given:
        def espd = new EspdDocument(childLabour: new CriminalConvictionsCriterion(exists: true, reason: "Reason_06 here"))

        when:
        def response = parseResponseXml(espd)
        def idx = getResponseCriterionIndex(ExclusionCriterion.CHILD_LABOUR)

        then:
        def subGroup = response.Criterion[idx].RequirementGroup[0].RequirementGroup[0]

        def req = subGroup.Requirement[1]
        checkRequirement(req, "7d35fb7c-da5b-4830-b598-4f347a04dceb", "Reason", "DESCRIPTION")
        req.Response.size() == 1
        req.Response[0].Description.text() == "Reason_06 here"
    }

    def "check the 'Who has been convicted' requirement response"() {
        given:
        def espd = new EspdDocument(childLabour: new CriminalConvictionsCriterion(exists: true, convicted: "Hodor_06 was convicted"))

        when:
        def response = parseResponseXml(espd)
        def idx = getResponseCriterionIndex(ExclusionCriterion.CHILD_LABOUR)

        then:
        def subGroup = response.Criterion[idx].RequirementGroup[0].RequirementGroup[0]

        def req = subGroup.Requirement[2]
        checkRequirement(req, "c5012430-14da-454c-9d01-34cedc6a7ded", "Who has been convicted", "DESCRIPTION")
        req.Response.size() == 1
        req.Response[0].Description.text() == "Hodor_06 was convicted"
    }

    def "check the 'Length of the period of exclusion' requirement response"() {
        given:
        def espd = new EspdDocument(childLabour: new CriminalConvictionsCriterion(exists: true, periodLength: "7 years"))

        when:
        def response = parseResponseXml(espd)
        def idx = getResponseCriterionIndex(ExclusionCriterion.CHILD_LABOUR)

        then:
        def subGroup = response.Criterion[idx].RequirementGroup[0].RequirementGroup[0]

        def req = subGroup.Requirement[3]
        checkRequirement(req, "9ca9096f-edd2-4f19-b6b1-b55c83a2d5c8", "Length of the period of exclusion", "PERIOD")
        req.Response.size() == 1
        req.Response[0].Period.Description[0].text() == "7 years"
    }

    def "check the 'Have you taken measures to demonstrate your reliability (\"Self-Cleaning\")' requirement response"() {
        given:
        def espd = new EspdDocument(childLabour: new CriminalConvictionsCriterion(exists: true,
                selfCleaning: new SelfCleaning(answer: false)))

        when:
        def response = parseResponseXml(espd)
        def idx = getResponseCriterionIndex(ExclusionCriterion.CHILD_LABOUR)

        then:
        def subGroup = response.Criterion[idx].RequirementGroup[0].RequirementGroup[0].RequirementGroup[0]

        def req = subGroup.Requirement[0]
        req.Response.size() == 1
        req.Response[0].Indicator.text() == "false"
    }

    def "check the 'Self cleaning description' requirement response"() {
        given:
        def espd = new EspdDocument(childLabour: new CriminalConvictionsCriterion(exists: true,
                selfCleaning: new SelfCleaning(description: "Hodor_06 is clean")))

        when:
        def response = parseResponseXml(espd)
        def idx = getResponseCriterionIndex(ExclusionCriterion.CHILD_LABOUR)

        then:
        def subGroup = response.Criterion[idx].RequirementGroup[0].RequirementGroup[0].RequirementGroup[0].RequirementGroup[0]

        def req = subGroup.Requirement[0]
        req.Response.size() == 1
        req.Response[0].Description.text() == "Hodor_06 is clean"
    }

    def "check the 'Is this information available electronically' requirement response"() {
        given:
        def espd = new EspdDocument(childLabour: new CriminalConvictionsCriterion(exists: true,
                availableElectronically: new AvailableElectronically(answer: false)))

        when:
        def response = parseResponseXml(espd)
        def idx = getResponseCriterionIndex(ExclusionCriterion.CHILD_LABOUR)

        then:
        def subGroup = response.Criterion[idx].RequirementGroup[1]

        def req = subGroup.Requirement[0]
        req.Response.size() == 1
        req.Response[0].Indicator.text() == "false"
    }

    def "check the 'Info electronically URL' requirement response"() {
        given:
        def espd = new EspdDocument(childLabour: new CriminalConvictionsCriterion(exists: true,
                availableElectronically: new AvailableElectronically(answer: true, url: "http://hodor_06.com")))

        when:
        def response = parseResponseXml(espd)
        def idx = getResponseCriterionIndex(ExclusionCriterion.CHILD_LABOUR)

        then:
        def subGroup = response.Criterion[idx].RequirementGroup[1].RequirementGroup[0]

        def req = subGroup.Requirement[0]
        req.Response.size() == 1
        checkEvidence(req.Response[0].Evidence, "http://hodor_06.com")
    }

    def "check the 'Info electronically code' requirement response"() {
        given:
        def espd = new EspdDocument(childLabour: new CriminalConvictionsCriterion(exists: true,
                availableElectronically: new AvailableElectronically(answer: true, code: "HODOR_06")))

        when:
        def response = parseResponseXml(espd)
        def idx = getResponseCriterionIndex(ExclusionCriterion.CHILD_LABOUR)

        then:
        def subGroup = response.Criterion[idx].RequirementGroup[1].RequirementGroup[0]

        def req = subGroup.Requirement[1]
        req.Response.size() == 1
        req.Response[0].Code.text() == "HODOR_06"
    }

}