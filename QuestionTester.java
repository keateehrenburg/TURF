/*Elliott Ettore cse11wbs A14113346
 *Keate Ehrenburg cse11wbo A15322099
 *Thalal Mohamed-Cassim cse11wkq A14003195
 *
 *This file compares the input between two
 *profiles (contained in testResponse1 and 2).
 *
 */

package prototype;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import com.fasterxml.jackson.core.JsonProcessingException;

import io.cortical.retina.rest.ApiException;


//This class declares both the questions and each
//users' input as variables to be compared
//within its methods.
public class QuestionTester {
	
	private static final String ABSOLUTE_QUESTIONS_PATH = "/Users/friedlurker/eclipse-workspace/prototype/src/questions.txt";
	private static final String ABSOLUTE_TEST_RESPONSE1_PATH = "/Users/friedlurker/eclipse-workspace/prototype/src/testResponse1.txt";
	private static final String ABSOLUTE_TEST_RESPONSE2_PATH = "/Users/friedlurker/eclipse-workspace/prototype/src/testResponse2.txt";

	//Our main method finds the similarity between each profile.
	public static void main(String[] args) throws ApiException, JsonProcessingException {
		ArrayList<String> questions = getFileEntries(ABSOLUTE_QUESTIONS_PATH);
		
		ArrayList<String> responses1 = getFileEntries(ABSOLUTE_TEST_RESPONSE1_PATH); // Gets each line from testResponse1.txt
		UserExpression[] profile1 = new UserExpression[questions.size()];
		
		ArrayList<String> responses2 = getFileEntries(ABSOLUTE_TEST_RESPONSE2_PATH); // Gets each line from testResponse2.txt
		UserExpression[] profile2 = new UserExpression[questions.size()];
		
		for (int i = 0; i < questions.size(); i++) {
			profile1[i] = new UserExpression(responses1.get(i));
			profile2[i] = new UserExpression(responses2.get(i));
		}
		
		System.out.println("Similarity between responses per question: ");
		for (double ent : compareProfiles(profile1, profile2)) {
			System.out.print("\t" + ent);
		}
		

	}

	//This method prints the similarity metric vector.
	private static void print2dArray(double[][] compareProfiles) {
		for (double[] row : compareProfiles) {
			System.out.print("[ ");
			for (double ent : row) {
				String entry = (ent == row[row.length-1]) ? ent + "\t\t\t" : ent + ",\t\t\t";
				System.out.print(entry);
			}
			System.out.println(" ]");
		}
		
	}

	//This method compares our user inputs.
	private static double[] compareProfiles(UserExpression[] profile1, UserExpression[] profile2) throws JsonProcessingException, ApiException {
		double[] similarity = new double[profile1.length]; // Used to be 2d array of metrics
		for (int i = 0; i < profile1.length; i++) { // TO-DO: Play with these metrics to tune the profile comparison
			//similarity[i][0] = profile1[i].compareTo(profile2[i]).getEuclideanDistance(); // Gets Euclidean Distance metric
			//similarity[i][1] = profile1[i].compareTo(profile2[i]).getCosineSimilarity();
			//similarity[i][2] = profile1[i].compareTo(profile2[i]).getJaccardDistance();
			//similarity[i][3] = profile1[i].compareTo(profile2[i]).getOverlappingLeftRight();
			//similarity[i][4] = profile1[i].compareTo(profile2[i]).getOverlappingRightLeft();
			similarity[i] = profile1[i].compareTo(profile2[i]).getWeightedScoring();// For now weighted scoring because its cortical's 
																					 // way of expressing both similarity AND confidence in
																					 // in the form of an innerproduct between vectors representing expression
																					 // being compared. This innerproduct is evident in comparing two pairs of
																					 // identical responses of varying lengths/nuance having a consistently lower
																					 // weighted sum when the identical responses being compared were of lower length
																					 // or nuance (ie the API's lack of confidence in the similarity scoring) than
																					 // other identical pairs of longer length and greater detail. 
		}
		return similarity; // Returns a similarity-metric vector with each question being a component. 
	}
	
	//This method is used to parse questions.txt and testResponse?.txt into List
	private static ArrayList<String> getFileEntries(String path) { // Used to parse questions.txt and testResponse?.txt into List
		ArrayList<String> entries = new ArrayList<String>();
		try { // read questions.txt file 
			File file = new File(path);
			FileReader fileReader = new FileReader(file);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				entries.add(line.toString());
			}
			fileReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return entries;
	}
	
}
