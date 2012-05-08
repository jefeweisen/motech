package org.motechproject.openmrs.rest.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.mrs.model.MRSEncounter;
import org.motechproject.mrs.model.MRSFacility;
import org.motechproject.mrs.model.MRSObservation;
import org.motechproject.mrs.model.MRSPatient;
import org.motechproject.mrs.model.MRSPerson;
import org.motechproject.mrs.model.MRSUser;
import org.motechproject.mrs.services.MRSEncounterAdapter;
import org.motechproject.openmrs.rest.HttpException;
import org.motechproject.openmrs.rest.RestfulClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationOpenMrsWS.xml" })
public class MRSEncounterAdapterImplIT extends AbstractAdapterImplIT {

	private static final String TEST_CONCEPT_NAME = "Test Concept";
	private static final String CONCEPT_PATH = "/ws/rest/v1/concept";
	private static final String ENCOUNTER_TYPE = "ADULTINITIAL";

	private MRSFacility facility;
	private MRSPatient patient;
	private String tempConceptUuid;
	private MRSUser creator;	
	
	@Autowired
	MRSEncounterAdapter encounterAdapter;

	@Autowired
	RestfulClient restfulClient;

	@Value("${openmrs.url}")
	String openmrsUrl;

	@Before
	public void before() {
		facility = null;
		patient = null;
		tempConceptUuid = null;
		creator = null;		
	}
	
	@Test
	public void shouldCreateEncounter() throws HttpException, URISyntaxException {
		MRSEncounter persistedEncounter = null;
		try {
			createRequiredEntities();
			
			persistedEncounter = createEncounterWithSingleObservation();
			assertNotNull(persistedEncounter.getId());
		} finally {
			deleteEncounter(persistedEncounter);
			deleteCreatedEntities();
		}
	}

	private MRSEncounter createEncounterWithSingleObservation() {
		MRSEncounter persistedEncounter;
		Set<MRSObservation> obs = new HashSet<MRSObservation>();
		MRSObservation ob = new MRSObservation(Calendar.getInstance().getTime(), TEST_CONCEPT_NAME, "Test Value");
		obs.add(ob);

		persistedEncounter = createEncounter(obs, currentDate);
		return persistedEncounter;
	}
	
	@Test
	public void shouldFindEncounter() throws HttpException, URISyntaxException {
		MRSEncounter persistedEncounter = null;
		try {
			createRequiredEntities();
			persistedEncounter = createEncounterWithSingleObservation();
			
			List<MRSEncounter> encounters = encounterAdapter.getAllEncountersByPatientMotechId(MOTECH_ID_1);
			assertEquals(1, encounters.size());
			
			MRSEncounter fetchedEncounter = encounters.get(0);
			assertEquals(persistedEncounter.getId(), fetchedEncounter.getId());
			// this property currently cannot be set through the web services
			assertNull(fetchedEncounter.getCreator());
			assertNotNull(fetchedEncounter.getProvider());
			assertNotNull(fetchedEncounter.getFacility());
			assertNotNull(fetchedEncounter.getPatient());
		} finally {
			deleteEncounter(persistedEncounter);
			deleteCreatedEntities();
		}
	}
	
	@Test 
	public void shouldFindLatestEncounter() throws HttpException, URISyntaxException {
		List<MRSEncounter> persistedEncounters = null;
		try {
			createRequiredEntities();
			persistedEncounters = createMultipleEncounterWithObservation();
			
			MRSEncounter encounter = encounterAdapter.getLatestEncounterByPatientMotechId(MOTECH_ID_1, null);
			assertEquals(currentDate, encounter.getDate());
		} finally {
			deleteEncounter(persistedEncounters.get(0));
			deleteEncounter(persistedEncounters.get(1));
			deleteCreatedEntities();
		}		
	}

	private List<MRSEncounter> createMultipleEncounterWithObservation() {
		List<MRSEncounter> encounters = new ArrayList<MRSEncounter>();
		MRSEncounter persistedEncounter;
		Set<MRSObservation> obs = new HashSet<MRSObservation>();
		MRSObservation ob = new MRSObservation(Calendar.getInstance().getTime(), TEST_CONCEPT_NAME, "Test Value");
		obs.add(ob);

		persistedEncounter = createEncounter(obs, currentDate);
		encounters.add(persistedEncounter);
		
		Calendar pastDate = Calendar.getInstance();
		pastDate.add(Calendar.DATE, -100);
		obs = new HashSet<MRSObservation>();
		ob = new MRSObservation(Calendar.getInstance().getTime(), TEST_CONCEPT_NAME, "Test Value");
		obs.add(ob);
		
		persistedEncounter = createEncounter(obs, pastDate.getTime());
		encounters.add(persistedEncounter);
		
		return encounters;
    }

	private MRSEncounter createEncounter(Set<MRSObservation> obs, Date encounterDate) {
		MRSEncounter encounter = new MRSEncounter(creator.getPerson().getId(), creator.getId(), facility.getId(),
				encounterDate, patient.getId(), obs, ENCOUNTER_TYPE);

		return encounterAdapter.createEncounter(encounter);		
	}

	private void createRequiredEntities() throws HttpException, URISyntaxException {
		facility = createTemporaryLocation();
		patient = createTemporaryPatient(MOTECH_ID_1, makePerson(), facility);
		tempConceptUuid = createTemporaryConcept();
		creator = createTemporaryProvider();
	}	
	
	private void deleteCreatedEntities() throws HttpException, URISyntaxException {
		deleteConcept(tempConceptUuid);
		deletePatient(patient);
		deleteUser(creator);
		deleteFacility(facility);
	}

	private String createTemporaryConcept() throws URISyntaxException, HttpException {
		//{"names":[{"name":"test concept", "locale": "en", "conceptNameType": "FULLY_SPECIFIED"}],"datatype":"Text","conceptClass":"Test"}
		URI uri = new URI(openmrsUrl + CONCEPT_PATH);
		ObjectNode conceptObj = JsonNodeFactory.instance.objectNode();
		
		ArrayNode names = JsonNodeFactory.instance.arrayNode();
		ObjectNode name = JsonNodeFactory.instance.objectNode();
		name.put("name", TEST_CONCEPT_NAME);
		name.put("locale", "en");
		name.put("conceptNameType", "FULLY_SPECIFIED");
		names.add(name);
		
		conceptObj.put("names", names);
		conceptObj.put("datatype", "Text");
		conceptObj.put("conceptClass", "Test");
		JsonNode result = restfulClient.postForJsonNode(uri, conceptObj);

		return result.get("uuid").asText();
	}

	private MRSUser createTemporaryProvider() throws URISyntaxException, HttpException {
		ObjectNode person = JsonNodeFactory.instance.objectNode();
		person.put("birthdate", "1970-01-01");
		person.put("gender", "M");
		ArrayNode node = JsonNodeFactory.instance.arrayNode();
		ObjectNode preferredName = JsonNodeFactory.instance.objectNode();
		preferredName.put("givenName", "Troy");
		preferredName.put("familyName", "Parks");
		node.add(preferredName);
		person.put("names", node);

		URI personUri = new URI(openmrsUrl + "/ws/rest/v1/person");
		JsonNode response = restfulClient.postForJsonNode(personUri, person);
		String personUuid = response.get("uuid").asText();

		ObjectNode userNode = JsonNodeFactory.instance.objectNode();
		userNode.put("username", "troy");
		userNode.put("password", "Testing123");
		userNode.put("person", personUuid);

		URI userUri = new URI(openmrsUrl + "/ws/rest/v1/user");
		response = restfulClient.postForJsonNode(userUri, userNode);

		return new MRSUser().id(response.get("uuid").asText()).person(new MRSPerson().id(personUuid));
	}

	private void deleteEncounter(MRSEncounter persistedEncounter) throws HttpException, URISyntaxException {
		if (persistedEncounter == null) return;
		restfulClient.deleteEntity(new URI(openmrsUrl + "/ws/rest/v1/encounter/" + persistedEncounter.getId() + "?purge"));
	}	
	
	private void deleteConcept(String tempConceptUuid) throws HttpException, URISyntaxException {
		if (tempConceptUuid == null) return;
		restfulClient.deleteEntity(new URI(openmrsUrl + CONCEPT_PATH + "/" + tempConceptUuid + "?purge"));
	}
	

	private void deleteUser(MRSUser creator) throws HttpException, URISyntaxException {
		if(creator == null) return;
		restfulClient.deleteEntity(new URI(openmrsUrl + "/ws/rest/v1/user/" + creator.getId() + "?purge"));
		restfulClient.deleteEntity(new URI(openmrsUrl + "/ws/rest/v1/person/" + creator.getPerson().getId() + "?purge"));
	}
}
