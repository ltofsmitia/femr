package femr.business.services;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.Query;
import com.google.inject.Inject;
import femr.business.dtos.ServiceResponse;
import femr.common.models.*;
import femr.data.daos.IRepository;
import femr.data.models.*;

import java.util.List;

public class SearchService implements ISearchService {
    private IRepository<IPatient> patientRepository;
    private IRepository<IPatientEncounter> patientEncounterRepository;
    private IRepository<IPatientEncounterVital> patientEncounterVitalRepository;
    private IRepository<IVital> vitalRepository;
    private IRepository<IPatientPrescription> patientPrescriptionRepository;
    private IRepository<IPatientEncounterTreatmentField> patientEncounterTreatmentFieldRepository;

    @Inject
    public SearchService(IRepository<IPatient> patientRepository,
                         IRepository<IPatientEncounter> patientEncounterRepository,
                         IRepository<IPatientEncounterVital> patientEncounterVitalRepository,
                         IRepository<IVital> vitalRepository,
                         IRepository<IPatientPrescription> patientPrescriptionRepository,
                         IRepository<IPatientEncounterTreatmentField> patientEncounterTreatmentFieldRepository) {
        this.patientRepository = patientRepository;
        this.patientEncounterRepository = patientEncounterRepository;
        this.patientEncounterVitalRepository = patientEncounterVitalRepository;
        this.vitalRepository = vitalRepository;
        this.patientPrescriptionRepository = patientPrescriptionRepository;
        this.patientEncounterTreatmentFieldRepository = patientEncounterTreatmentFieldRepository;
    }

    @Override
    public ServiceResponse<IPatient> findPatientById(int id) {
        ExpressionList<Patient> query = getPatientQuery().where().eq("id", id);
        IPatient savedPatient = patientRepository.findOne(query);

        ServiceResponse<IPatient> response = new ServiceResponse<>();
        if (savedPatient == null) {
            response.addError("id", "id does not exist");
        } else {
            response.setResponseObject(savedPatient);
        }
        return response;
    }

    @Override
    public ServiceResponse<IPatientEncounter> findPatientEncounterById(int id) {
        ExpressionList<PatientEncounter> query = getPatientEncounterQuery().where().eq("id", id);
        IPatientEncounter patientEncounter = patientEncounterRepository.findOne(query);

        ServiceResponse<IPatientEncounter> response = new ServiceResponse<>();
        if (patientEncounter == null) {
            response.addError("id", "id does not exist");
        } else {
            response.setResponseObject(patientEncounter);
        }
        return response;
    }

    @Override
    public ServiceResponse<IPatientEncounter> findCurrentEncounterByPatientId(int id) {
        ExpressionList<PatientEncounter> query =
                getPatientEncounterQuery().where().eq("patient_id", id);
        List<? extends IPatientEncounter> patientEncounters = patientEncounterRepository.find(query);

        ServiceResponse<IPatientEncounter> response = new ServiceResponse<>();
        if (patientEncounters.size() < 1) {
            response.addError("id", "No encounters exist for that id");
        } else {
            int size = patientEncounters.size();
            response.setResponseObject(patientEncounters.get(size - 1));
        }
        return response;
    }

    @Override
    public ServiceResponse<IPatient> findPatientByName(String firstName, String lastName) {
        ExpressionList<Patient> query = getPatientQuery().where().eq("first_name", firstName).eq("last_name", lastName);
        IPatient savedPatient = patientRepository.findOne(query);

        ServiceResponse<IPatient> response = new ServiceResponse<>();
        if (savedPatient == null) {
            response.addError("first name/last name", "patient could not be found by name");
        } else {
            response.setResponseObject(savedPatient);
        }
        return response;
    }

    @Override
    public ServiceResponse<IPatientEncounterVital> findPatientEncounterVitalByVitalIdAndEncounterId(int vitalId, int encounterId) {
        ExpressionList<PatientEncounterVital> query = getPatientEncounterVitalQuery().where().eq("vital_id", vitalId).eq("patient_encounter_id", encounterId);
        IPatientEncounterVital patientEncounterVital = patientEncounterVitalRepository.findOne(query);

        ServiceResponse<IPatientEncounterVital> response = new ServiceResponse<>();
        if (patientEncounterVital == null) {
            response.addError("patientEncounterVital", "could not find vital");
        } else {
            response.setResponseObject(patientEncounterVital);
        }
        return response;
    }

    @Override
    public List<? extends IPatientEncounter> findAllEncountersByPatientId(int id) {
        ExpressionList<PatientEncounter> query = getPatientEncounterQuery().where().eq("patient_id", id);
        List<? extends IPatientEncounter> patientEncounters = patientEncounterRepository.find(query);
        return patientEncounters;
    }

    @Override
    public List<? extends IPatientPrescription> findPrescriptionsByEncounterId(int id) {
        ExpressionList<PatientPrescription> query = getPatientPrescriptionQuery().where().eq("encounter_id", id);
        List<? extends IPatientPrescription> patientPrescriptions = patientPrescriptionRepository.find(query);
        return patientPrescriptions;
    }

    @Override
    public List<? extends IPatientEncounterTreatmentField> findProblemsByEncounterId(int id) {
        ExpressionList<PatientEncounterTreatmentField> query = getPatientEncounterTreatmentFieldQuery().where().eq("patient_encounter_id", id).eq("treatment_field_id", 2);
        List<? extends IPatientEncounterTreatmentField> patientEncounterTreatmentFields = patientEncounterTreatmentFieldRepository.find(query);
        return patientEncounterTreatmentFields;
    }

    @Override
    public List<? extends IVital> findAllVitals() {
        List<? extends IVital> vitals = vitalRepository.findAll(Vital.class);
        return vitals;
    }

    private Query<Patient> getPatientQuery() {
        return Ebean.find(Patient.class);
    }

    private Query<PatientEncounter> getPatientEncounterQuery() {
        return Ebean.find(PatientEncounter.class);
    }

    private Query<PatientEncounterVital> getPatientEncounterVitalQuery() {
        return Ebean.find(PatientEncounterVital.class);
    }

    private Query<PatientPrescription> getPatientPrescriptionQuery() {
        return Ebean.find(PatientPrescription.class);
    }

    private Query<PatientEncounterTreatmentField> getPatientEncounterTreatmentFieldQuery() {
        return Ebean.find(PatientEncounterTreatmentField.class);
    }
}