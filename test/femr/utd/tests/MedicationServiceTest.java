package femr.utd.tests;

import com.google.inject.Inject;
import femr.business.services.core.IMedicationService;
import femr.business.services.system.MedicationService;
import femr.common.dtos.ServiceResponse;
import femr.common.models.MedicationItem;
import femr.common.models.PrescriptionItem;
import femr.data.models.core.IMedication;
import femr.data.models.mysql.PatientPrescription;
import org.junit.*;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * Unit Tests for MedicationService
 */
public class MedicationServiceTest extends BaseTest {

    private static IMedicationService service;
    private static MedicationItem newMed;


    @Inject
    public void setService(IMedicationService service) {
        this.service = service;
    }

    @BeforeClass
    public static void initialize() {
        System.out.println("Testing MedicationService!!");
        cleanDB();
    }

    @AfterClass
    public static void cleanDB() {

        //clean the DB
        if (newMed != null) {
            //System.out.println("Cleaning!");
            //remove the new medication
            ServiceResponse<MedicationItem> response2 = service.removeMedication(newMed.getId());
            checkForErrors(response2);

            //assert deletion
            MedicationItem medDeleted = response2.getResponseObject();
            assertNull(medDeleted);
        }
    }

    @Test
    public void testRetrieveAllMedications() throws Exception {
        //retrieve all the medications
        ServiceResponse<List<String>> response = service.retrieveAllMedications();

        //check for errors
        checkForErrors(response);

        //get the list of medications
        List<String> medications = response.getResponseObject();

        //assert the number of medications

        int numMeds = 3351 + (newMed != null ? 1 : 0);

        //System.out.println("NumMeds:"+numMeds);
        //System.out.println("Newmed==null:"+(newMed==null));
        //System.out.println("medications.size():"+medications.size());
        System.out.println(newMed != null);
        assertEquals(numMeds, medications.size());


    }



    @Test
    public void testCreateMedication(){

        //create the medication
        ServiceResponse<MedicationItem> response = service.createMedication("Medication 1", "formtest", null);

        //check for errors
        checkForErrors(response);

        //get the new medication
        newMed = response.getResponseObject();

        //assert the new medication is not null
        assertNotNull(newMed);

    }

    @Test
    public void testDeleteMedication(){

        //create the medication
        ServiceResponse<MedicationItem> response = service.createMedication("Medication 2", "formtest2", null);


        //check for errors
        checkForErrors(response);

        //get the new medication
        MedicationItem tempMed = response.getResponseObject();

        //get the medication ID
        int id = tempMed.getId();

        //delete the medication
        ServiceResponse<MedicationItem> response1 = service.deleteMedication(id);

        //check for errors
        checkForErrors(response1);


        ServiceResponse<MedicationItem> response2;
        ServiceResponse<IMedication> retrieveResponse = service.retrieveMedication(id);

        checkForErrors(retrieveResponse);

        long ans = (long) ((retrieveResponse.getResponseObject().getIsDeleted().booleanValue())? 1 : 0);

        ServiceResponse<MedicationItem> remresponse = service.removeMedication(tempMed.getId());
        checkForErrors(remresponse);

        //assert deletion
        MedicationItem medDeleted = remresponse.getResponseObject();
        tempMed= null;

        //assertEquals
        assertEquals(ans,1);

    }

    //test case: search femr_testDB for all prescriptions under the encounterId of 83
    //Of which there should be 4
    @Test
    public void testRetrieveAllPatientPrescriptionsByEncounterId() throws Exception{
            //retrieve all the prescriptions by encounterId
            ServiceResponse<List<PatientPrescription>> response = ((MedicationService)service)
                    .retrieveAllPatientPrescriptionsByEncounterId(83);

            //check for errors
            checkForErrors(response);

            //get the list of prescriptions
            //based on femr_testdb we should get a list of four different PatientPrescriptions
            List<PatientPrescription> patientPrescriptions = response.getResponseObject();

            //assert the number of prescriptions
            assertEquals(4, patientPrescriptions.size());

    }

    //creates a new patientPrescription that is to be removed immediately after
    @Test
    public void testRemovePatientPrescription(){
//ServiceResponse<PrescriptionItem> createPrescription(int medicationId, Integer administrationId,
// int encounterId, int userId, int amount, String specialInstructions)
        //ServiceResponse<PrescriptionItem> response = service.createPrescription(
        //        19, null, 84, 6, 0, null
        //);
        ServiceResponse<PrescriptionItem> response = service.createPrescription(
                19, null, 1575, 6, 0, null
        );

        //check for errors
        checkForErrors(response);

        //get the new prescription
        PrescriptionItem p = response.getResponseObject();

        //get the medication ID
        int id = p.getId();

        //remove the prescription
        ServiceResponse<PatientPrescription> response1 = ((MedicationService)service).removePatientPrescription(id);

        //check for errors
        checkForErrors(response1);


        /*ServiceResponse<MedicationItem> response2;
        ServiceResponse<IMedication> retrieveResponse = service.retrievePresc(id);

        checkForErrors(retrieveResponse);

        long ans = (long) ((retrieveResponse.getResponseObject().getIsDeleted().booleanValue())? 1 : 0);

        ServiceResponse<MedicationItem> remresponse = service.removeMedication(tempMed.getId());
        checkForErrors(remresponse);*/

        //assert deletion
        //MedicationItem medDeleted = remresponse.getResponseObject();


        //assertEquals
        assertEquals(p, null);

    }

}