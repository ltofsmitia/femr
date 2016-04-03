package femr.utd.tests;

import com.google.inject.Inject;
import femr.business.services.core.IMedicationService;
import femr.common.dtos.ServiceResponse;
import femr.common.models.MedicationItem;
import femr.data.models.core.IMedication;
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
    public static void initialize(){
        System.out.println("Testing MedicationService!!");
        cleanDB();
    }

    @AfterClass
    public static void cleanDB(){

        //clean the DB
        if (newMed!=null) {
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

        int numMeds = 3351 + (newMed!=null ? 1 : 0);

        //System.out.println("NumMeds:"+numMeds);
        //System.out.println("Newmed==null:"+(newMed==null));
        //System.out.println("medications.size():"+medications.size());

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

}