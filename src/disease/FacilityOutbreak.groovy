package disease;
import agentcontainers.Facility;
import agents.Person;
import org.apache.commons.math3.distribution.ExponentialDistribution
import org.apache.commons.math3.ode.events.EventHandler

import repast.simphony.engine.schedule.ISchedulableAction
import repast.simphony.engine.schedule.ISchedule;
import repast.simphony.engine.schedule.ScheduleParameters;
import repast.simphony.engine.schedule.ScheduledMethod;
public class FacilityOutbreak {
   public Disease disease;
   public Facility facility;
   double numSusceptibleNow = 0;
   int numColonizedNow = 0;
   double numSusceptibleEffective;
   double numContagiousEffective;
   public int transmissionsTally = 0;               
   int numAdmissionsColonized = 0;
   double importationRate;
   double prevalence;
   public String diseaseName;
   int numAdmissionsTallied = 0;
   int popTallied = 0;
   double popTalliedColonized = 0;
   double avgPrevalence;
   int numSusceptibleNonIsoNow = 0;
   int numSusceptibleIsoNow = 0;
   int numColonizedNonIsoNow = 0;
   int numColonizedIsoNow = 0;
   double clinicalDetectionsTallied;
   double clinicalDetectionsPer10000PatientDays;
   int dischargesTallied = 0;
   int colonizedDischargesTallied = 0;
   double avgDischargePrevalence;
   double transmissionRate = 0.0;
   private Object transmissionEvent;
   private ISchedule schedule;
   boolean stop = false
   ISchedulableAction nextAction;
   ExponentialDistribution distro;
   double meanIntraEventTime;
   public FacilityOutbreak(double intra_event_time) {
	   if (intra_event_time > 0) {
		   if (repast.simphony.engine.environment.RunEnvironment.getInstance() != null) {
			   schedule = repast.simphony.engine.environment.RunEnvironment.getInstance().getCurrentSchedule()
		   } else {
		   println("RunEnvironment is not initialized. Schedule will be set later.")
		   schedule = null
	   }
	   meanIntraEventTime = intra_event_time
	   distro = new ExponentialDistribution(intra_event_time)
   }
   }
   
   public FacilityOutbreak(Disease disease2) {
	disease = disease2;
}

public void transmission() {
	   stop = false
	   double currTime = schedule.getTickCount()
	   double elapse = distro.sample()
	   ScheduleParameters params = ScheduleParameters.createOneTime(currTime + elapse)
	   nextAction = schedule.schedule(params, this, "doTransmission")
   }
   public void doTransmission() {
	   System.out.println("Hi")
       PersonDisease pdS = null;
       PersonDisease pdC = null;
       double unifS = uniform() * numSusceptibleEffective;
       double unifC = uniform() * numContagiousEffective;
       double uS = 0.0;
       double uC = 0.0;
       for (Person p : facility.currentPatients) {
           PersonDisease pd = p.diseases.get(disease.simIndex);
           if (pd.colonized && uC < unifC) {
               uC += pd.transmissionRateContribution;
               if (uC > unifC)
					pdC = pd;
					System.out.println("pdC set");
           }
           if (!pd.colonized && uS <= unifS) {
               uS += pd.transmissionRateContribution;
               if (uS > unifS)
					pdS = pd;
					System.out.println("pdS set");
           }
		   if(pdS!=null) {
			   pdS.colonize();
			   pdS.addAcquisition();
		   }
           if (uC > unifC && uS > unifS) break;   
       }
       if (pdC == null || pdS == null) {
           error("Transmission pair choice failure\nuS = %f; unifS = %f; nS = %f;\nuC = %f; unifC = %f; nC = %f\n",
                   uS, unifS, numSusceptibleEffective, uC, unifC, numContagiousEffective);
       }
       
       if (pdC.initialInfection) facility.region.numTransmissionsFromInitialCase++;
       transmissionsTally++;
   }
   public void updateTransmissionRate() {
       double newTransmissionRate;
       /*
       C = colonized
       S = susceptible
       I = isolated
       */
       int nC = 0;
       int nS = 0;
       int nCI = 0;
       int nSI = 0;
       double cScore = 0.0;
       double sScore = 0.0;
       for (Person p : facility.currentPatients) {
           PersonDisease pd = p.diseases.get(disease.simIndex);
           if (pd.colonized) {
               cScore += pd.transmissionRateContribution;
               if (p.isolated) nCI++;
               else nC++;
           } else {
               sScore += pd.transmissionRateContribution;
               if (p.isolated) nSI++;
               else nS++;
           }   
       }
       numSusceptibleNonIsoNow = nS;
       numColonizedNonIsoNow = nC;
       numSusceptibleIsoNow = nSI;
       numColonizedIsoNow = nCI;
       numSusceptibleNow = nS + nSI;
       numColonizedNow = nC + nCI;
       prevalence = 1.0 * numColonizedNow / facility.currentPopulationSize;
       numContagiousEffective = cScore;
       numSusceptibleEffective = sScore;
       newTransmissionRate = disease.getBaselineBetaValue(facility.type) * numContagiousEffective * numSusceptibleEffective / facility.currentPatients.size();
       setTransmissionRate(newTransmissionRate);
   }
   public void setTransmissionRate(double newTransmissionRate) {
       this.transmissionRate = newTransmissionRate;
       if (transmissionEvent != null) {
           if (transmissionRate != newTransmissionRate) {
               schedule.setFinishing(true);
               transmissionEvent = null;
           }
       }
       if (transmissionRate > 0) {
           ScheduleParameters params = ScheduleParameters.createRepeating(
               schedule.getTickCount(),
               1.0,                  
               1.0                   
           );
           transmissionEvent = schedule.schedule(params, this, "doTransmission");
       }
   }
   private void error(String message, double... values) {
       System.err.printf(message, values);
   }
   int getNumColonized() {
       return numColonizedNow;
   }
   void updatePrevalenceTally() {
       popTallied += facility.currentPopulationSize;
       popTalliedColonized += numColonizedNow;
       avgPrevalence = 1.0 * popTalliedColonized / popTallied;
   }
   void updateStayTally(PersonDisease pd) {
       if (pd.clinicallyDetectedDuringCurrentStay) {
           clinicalDetectionsTallied++;
       }
       clinicalDetectionsPer10000PatientDays = 10000 * clinicalDetectionsTallied / facility.patientDays;
       dischargesTallied++;
       if (pd.colonized) colonizedDischargesTallied++;
       avgDischargePrevalence = 1.0 * colonizedDischargesTallied / dischargesTallied;
   }
   void updateAdmissionTally(PersonDisease pd) {
       if (pd.colonized) {
           numAdmissionsColonized++;
       }
       importationRate = 1.0 * numAdmissionsColonized / facility.numAdmissions;
   }
   double uniform() {
       return Math.random();
   }
   void setFacility(Facility f) {
	   facility = f;
   }
}
