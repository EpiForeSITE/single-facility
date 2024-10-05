package disease

import agents.Person

public class Disease {

	public static CRE = 1;
	public int type;
	public int simIndex;


	double getBaselineBetaValue(int facilityType){
		double acuteCareBeta = -1.0;
		double longTermAcuteCareBeta = -1.0;
		double nursingHomeBeta = -1.0;
		double nhReduction;

		if(type == CRE){
			longTermAcuteCareBeta = 0.0615;
			acuteCareBeta = 0.06;
			nhReduction = 10.0;
			nursingHomeBeta = acuteCareBeta / nhReduction;
		}

		double betaVal = 0.0;

		if(facilityType == 0) betaVal = longTermAcuteCareBeta;
		else if(facilityType == 1) betaVal = acuteCareBeta;
		else if(facilityType == 2) betaVal = nursingHomeBeta;

		return betaVal;
	}

	double getMeanTimeToClinicalDetection(int facilityType){
		double acuteCareMean = -1.0;
		double longTermAcuteCareMean = -1.0;
		double nursingHomeMean = -1.0;
		double nhChangeFactor = 1.0;


		if(type == CRE){
			acuteCareMean = 122.0;
			nhChangeFactor = 8.0;
			longTermAcuteCareMean = 106.0;
			nursingHomeMean = acuteCareMean * nhChangeFactor;
		}

		double t = 0.0;

		if(facilityType == 0) t = longTermAcuteCareMean;
		else if(facilityType == 1) t = acuteCareMean;
		else if(facilityType == 2) t = nursingHomeMean;

		return t;
	}


	String getDiseaseName(){
		if(type==CRE) return "CRE";
		return "";
	}

	double getAvgDecolonizationTime(){
		if(type == CRE) return 387.0;
		return 0;
	}

	double getProbSurveillanceDetection(){
		return 0.8
	}

	boolean allowImportationsDuringBurnIn(){
		if(type == CRE) return false;
	}

	boolean isolatePatientWhenDetected(){
		if(type == CRE) return true;
		return false;
	}

	boolean isActiveSurveillanceAgent(){
		if(type == CRE) return true;
		return false;
	}

	double getImportationProb(){
		return 0.206;
	}
	int getSimIndex() {
		return simIndex;
	}

	public void setType(int diseaseType) {
		type = diseaseType
	}
}