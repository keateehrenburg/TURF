/*Elliott Ettore cse11wbs A14113346
 *Keate Ehrenburg cse11wbo A15322099
 *Thalal Mohamed-Cassim cse11wkq A14003195
 *
 *This file contains the UserExpression to which
 *individual input is stored, as well as a large variety
 *of methods for analysis and comparison.
 */

package prototype;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;

import io.cortical.retina.client.FullClient;
import io.cortical.retina.model.Context;
import io.cortical.retina.model.ExpressionFactory;
import io.cortical.retina.model.Fingerprint;
import io.cortical.retina.model.Metric;
import io.cortical.retina.model.Term;
import io.cortical.retina.model.Text;
import io.cortical.retina.rest.ApiException;

//This class utilizes the Retina API to create a profile
//for each user to store and compare information.
public class UserExpression {
	
	private FullClient client;
	public Text text;
	public List<String> keywords;
	
	//Our API Key
	private static final String CORTICALIO_API_KEY = "b285cbb0-09df-11e8-8fa0-312111a3090a";
	
	//This constructor stores the user input for each response.
	public UserExpression(String response) throws ApiException {
		client = new FullClient(CORTICALIO_API_KEY, "en_associative");
		text = ExpressionFactory.text(response);
		keywords = client.getKeywordsForText(response);
	}
	// Public Instance method for updating user-entered text for parsing
	public void setText(String updatedText) throws ApiException {
		text = ExpressionFactory.text(updatedText);
		keywords = client.getKeywordsForText(updatedText);
	}
	
	// Public instance method for expression comparison
	public Metric compareTo(UserExpression expr) throws JsonProcessingException, ApiException {
		Metric met = client.compare(text, expr.text);
		return met;
	}
	
	@SuppressWarnings("unused")
	private List<List<Context>> getKeySubContexts(List<String> words) throws ApiException {
		List<List<Context>> subContexts = new ArrayList<List<Context>>();
		for (int i = 0; i < words.size(); i++) {
			subContexts.add(client.getContextsForTerm(words.get(i)));
		}
		return subContexts;
	}

	@SuppressWarnings("unused")
	private List<Term> getKeyTerms(List<String> words) {
		List<Term> terms = new ArrayList<Term>();
		for (int i = 0; i < words.size(); i++) {
			terms.add(ExpressionFactory.term(words.get(i)));
		}
		return terms;
	}

	public UserExpression clone() {
		try {
			return new UserExpression(this.text.getText());
		} catch (ApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	//This method prints the components of the user's response
	public void printProperties() throws ApiException { // Print 'response' components
		
		// Print 'text' component
		System.out.println("---Response FingerPrint---");
		System.out.println("Response Text: ");
		System.out.print("\t");
		System.out.println(text.getText());
		// Print 'keywords' component
		System.out.println("Response Keywords: ");
		System.out.print("\t");
		print_list(keywords);
		System.out.println();
		// Print 'keyTerms' component
		//System.out.println("Key Terms: ");
		//System.out.print("\t");
		//getFingerPrintsFromTerms(keyterms));
		//System.out.println();
		
	}
	
	//This method returns the semantic fingerprint of a given text.
	@SuppressWarnings("unused")
	private List<Fingerprint> getFingerPrintsFromTerms(List<Term> keytermls) throws ApiException {
		List<Fingerprint> fingerPrintLs = new ArrayList<Fingerprint>();
		for (Term keyTerm : keytermls) {
			fingerPrintLs.add(client.getFingerprintForText(keyTerm.getTerm()));
		}
		return fingerPrintLs;
	}
	@SuppressWarnings("unused")
	private void print_array(int[] arr) {
		System.out.print("[ ");
		for (int elm : arr) {
			System.out.print(elm + ", ");
		}
		System.out.println(" ]");
	}
	private <E> void print_list(List<E> arr) {
		System.out.print("[ ");
		for (E elm : arr) {
			if (elm instanceof Term) {
				System.out.print(((Term) elm).getTerm() + ", ");
			} else if (elm instanceof String) {
				System.out.print(elm + ", ");
			} else if (elm instanceof List<?>) {
				System.out.print("[ ");
				print_list((List<?>) elm);
				System.out.print(" ]");
			} else if (elm instanceof Context) {
				System.out.print(elm + ", ");
			}
		}
		System.out.println(" ]");
	}
	//Our main method runs as an example
	public static void main(String[] args) throws ApiException, JsonProcessingException {
		UserExpression cont = new UserExpression("In mathematics, the modular group is the projective special linear group PSL(2,Z) of 2 x 2 matrices with integer coefficients and unit determinant. The matrices A and -A are identified. The modular group acts on the upper-half of the complex plane by fractional linear transformations, and the name \"modular group\" comes from the relation to moduli spaces and not from modular arithmetic.");
		
		Metric met = cont.compareTo(new UserExpression("In algebraic geometry, a moduli space is a geometric space (usually a scheme or an algebraic stack) whose points represent algebro-geometric objects of some fixed kind, or isomorphism classes of such objects. Such spaces frequently arise as solutions to classification problems: If one can show that a collection of interesting objects (e.g., the smooth algebraic curves of a fixed genus) can be given the structure of a geometric space, then one can parametrize such objects by introducing coordinates on the resulting space. In this context, the term \"modulus\" is used synonymously with \"parameter\"; moduli spaces were first understood as spaces of parameters rather than as spaces of objects."));
		System.out.println("Cosine Similarity between Wikipedia pages: " + met.getCosineSimilarity());
		
	
	}
}
