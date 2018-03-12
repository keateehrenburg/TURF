/*Elliott Ettore cse11wbs A14113346
 *Keate Ehrenburg cse11wbo A15322099
 *Thalal Mohamed-Cassim cse11w-- A--
 *
 *This file constructs the GUI of the program. It finds
 *and prints each question, and then stores the input given
 *from each.
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import io.cortical.retina.rest.ApiException;
import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import prototype.UserExpression;


//This class uses multithreading to communicate with the cloud-based
//Retina API. It provides the GUI, questions, and input storage.
public class QuestionManager extends Application {

	private static final String PATH_TO_QUESTION = "/Users/friedlurker/eclipse-workspace/prototype/src/questions.txt";
	private UserExpression expression;
	private TextArea textArea;
	private Label question;
	private ArrayList<String> questions;
	private int indexOfQuestion = 0;
	private String[][] responses;
	private UserExpression[] profile;
	private BorderPane topPane;
	
	private Task<Void> populate = new Task<Void>() { // Had to override call method in task
		@Override public Void call() throws ApiException {
    			QuestionManager.this.updateText();
    			return null;
		}
	};
	
	// This method pulls a string from a file and creates a
	// BufferedReader to parse the responses, line for line.
	private void sampleFromFile(String path) throws IOException {
		File file = new File(path);
		FileReader fileReader = new FileReader(file);
		BufferedReader bufferedReader = new BufferedReader(fileReader);
		String line;
		while ((line = bufferedReader.readLine()) != null) {
			questions.add(line.toString());
		}
		fileReader.close();
	}
	
	// This method creates the "Next" button for our GUI. It allows
	// the user to progress to the next question.
	private Button createNextButton(BorderPane topPane) {

		Button nextButton = new Button();
		nextButton.setText("Next");
		nextButton.setPadding(new Insets(45, 12, 45, 12));
		topPane.setRight(nextButton);

		nextButton.setOnAction(new EventHandler<ActionEvent>() {
		    @Override public void handle(ActionEvent e) {
					    	try {
								saveResponse();
							} catch (ApiException e1) {
								e1.printStackTrace();
							}
		    		if (indexOfQuestion + 1 < questions.size()) {
		    			setUpNextQuestion();
		    		} else {
		    			for (String[] pair : responses) {	
		    				System.out.println("Question: " + pair[0]);
		    				System.out.println("Response: " + pair[1]);
		    			}
		    		}
		    }
		});
		return nextButton;
	}

	// This method creates the "Submit" button. It allows the
	// user to submit completed work.
	private Button createSubmitButton(BorderPane topPane) throws ApiException {
		Button submitButton = new Button();
		submitButton.setText("Submit");
		submitButton.setPadding(new Insets(45, 12, 45, 12));
		topPane.setLeft(submitButton);
		
		submitButton.setOnAction(new EventHandler<ActionEvent>() {
		    @Override public void handle(ActionEvent e) {
				try {
					if (indexOfQuestion + 1 < questions.size()) {
						return;
					}
					for (int i = 0; i < profile.length; i++) {
						String thisResponse = (!responses[i][1].isEmpty()) ? responses[i][1] : "1984"; // Default response if no response was entered
						profile[i] = new UserExpression(thisResponse);
						profile[i].printProperties();
					}
				} catch (ApiException e1) {
					e1.printStackTrace();
				}
		    	}
		});
		
		return submitButton;
	}
	
	// This method saves the response from the user to a given question and creates a background
	// thread to interface with Cortical.io to generate the UserExpression for the response to be held in 'profile'.
	private void saveResponse() throws ApiException {
		String thisQuestion = question.getText();
		String response = textArea.getText();
		// Generating the profile requires querying the cloud API for an Expression for each response
		// since this is a slow process we used multithreading to process the interfacing in the background
		// while the user responds to the following questions.
		Thread thread = new Thread(populate);
		thread.setDaemon(true);
		thread.start();	
		responses[indexOfQuestion] = new String[]{thisQuestion, response};
	}
	
	//This method invokes the next question.
	private void setUpNextQuestion() {
		indexOfQuestion++;
		question.setText(questions.get(indexOfQuestion));
		textArea.setText("");
	}

	//This method clears the user input field.
	private void updateText() throws ApiException {
		String temp = textArea.getText();
		if (!temp.isEmpty()) {
			expression.setText(temp);
		}
	}
	
	// This method initializes the GUI for the user to view and respond to the questions.
	public void start(Stage stage) throws ApiException, IOException {
		
		expression = new UserExpression("1984"); // Default UserExpression string
		
		questions = new ArrayList<String>();
		
		try { // read questions.txt file 
			sampleFromFile(PATH_TO_QUESTION);
		} catch (IOException e) {
			e.printStackTrace();
		}
		responses = new String[questions.size()][2];
		profile = new UserExpression[questions.size()];
		
		// Set up top level pane
		topPane = new BorderPane();
		Scene scene = new Scene(topPane); // Setting the Scene
		stage.setTitle("Triton Roommate Finder"); 
		stage.setScene(scene);
		
		//Create the space to provide the question
	    HBox header = new HBox();
	    header.setPadding(new Insets(45, 12, 45, 12));
	    topPane.setTop(header);
	    question = new Label(questions.get(0));
	    question.setFont(Font.font ("Verdana", 20));
	    question.setWrapText(true);
	    
	    //Create the user response field
	    textArea = new TextArea("Respond Here :)");
	    textArea.setEditable(true);
	    textArea.setWrapText(true);
	    textArea.setFont(Font.font ("Verdana", 15));
	    topPane.setCenter(textArea);

		header.getChildren().add(question);

		createNextButton(topPane);
		createSubmitButton(topPane);
		
		// Make stage visible
		stage.setWidth(400);
		stage.setHeight(600);
		stage.show();		
	}
	
	// Needed for running the javafx application
	public static void main(String [] args){
		Application.launch(args);
	}
}
