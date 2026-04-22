package application.client;

import application.core.UdpTransmitter;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

public class Chat 
{
	private static Chat instance; 			// class wide instance of chat
	private String [] chat;					// array to hold chat strings
    private GridPane grid = new GridPane();	// displayable gridpane

	// chat instance
	private Chat(int size)
	{
		// initialize chat fields
		this.chat = new String [size];
		this.grid = instantiateGridPane();   
	}
	
	// singleton design pattern
	public static Chat getInstance()
	{
		if (instance == null) 
			instance = new Chat(10);
		return instance;
	}
	
	public GridPane getGridPane()
	{
		return grid;
	}

	// updates grid pane chat with chat string array and returns it
	public GridPane updateGridPaneChat()
	{       
		grid.setHgap(10); // Horizontal spacing between columns
		grid.setVgap(5);  // Vertical spacing between rows
		
		// remove all previous chats
        grid.getChildren().removeIf(node -> {
            Integer row = GridPane.getRowIndex(node);
            int r = (row == null) ? 0 : row;
            return r >= 0 && r <= 9;
        });
        
		// iterate over chats and display them
        for (int i = 0; i < 10; i++) {
            // gridpane cells must be final, so ensure the i is passed into final currRow
            final int currRow = i;
            
    		// put msgs
            Label msgLbl = new Label(chat[9-i]);
            
            // makes it rainbow
            // hsb - Hue, Sat, Bright, hue changes with row, rest are constant
            msgLbl.setTextFill(Color.hsb(currRow * 36, 1.0, 1.0));	
            msgLbl.setStyle("-fx-font-family: Menlo; -fx-background-color: black;");
            grid.add(msgLbl, 0, i);

        }        
        return grid;
	}
	private GridPane instantiateGridPane()
	{
		/*
		 * Build Grid Pane (chat boxes are updated on msg received, chatSubmitBox is made once and never altered)
		 */
		GridPane rGrid = updateGridPaneChat();
        
		// add  input text field, submit button, and chat label. Through them in a horizontal box
        HBox chatSubmitBox = new HBox();

        // input field
        TextField inputField = new TextField();
        inputField.setPromptText("Enter message..."); 
        inputField.setStyle("-fx-text-fill: limegreen;  -fx-font-family: Menlo; -fx-control-inner-background: black;");

        // submit button
        Button submitButton = new Button("Send");
        submitButton.setStyle("-fx-text-fill: limegreen;  -fx-font-family: Menlo; -fx-background-color: black;");
        submitButton.setOnAction(event -> {
            String text = inputField.getText();
            if (!text.isEmpty()) 
            {                
                UdpTransmitter.getInstance().send(Main.serverAddress, Main.port, text);
                inputField.clear(); 
            }
            
        });

        // chat label
        Label chatLbl = new Label("Chat:");
        chatLbl.setStyle("-fx-text-fill: limegreen;  -fx-font-family: Menlo; -fx-background-color: black;");
        
        // add all children to chatSubmitBox and add chat submit box to grid pane for the first and only time
        chatSubmitBox.getChildren().addAll(chatLbl, inputField, submitButton);
        rGrid.add(chatSubmitBox, 0, 11); 
        
        return rGrid;
	}
	
	// add msg string to chat string array
	public void add(String msg)
	{
    	// bump the 10th msg off 
    	// shuffle the remaining chats
    	for(int i = 9; i > 0; --i)
    	{
    		chat[i] = chat[i - 1];	// move lower msgs up
    	}
    	chat[0] = msg;				// insert new msg at 0th position
	}
	
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		for(int i = 9; i > -1; i--)
		{
			sb.append((chat[i] == null) ? i + ". \n" : (String.format("%d. %s\n",i, chat[i])));
		}
		return sb.toString();
	}
	
	
}
