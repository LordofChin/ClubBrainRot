package application.client;

import application.core.UdpTransmitter;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

public class Chat {
	public String [] chat;
	private static Chat instance;
    GridPane grid = new GridPane();

	
	private Chat(int size)
	{
		this.chat = new String [size];
		
		grid = new GridPane();
		getGridPane();
        
        TextField inputField = new TextField();
        inputField.setPromptText("Enter message..."); 

        Button submitButton = new Button("Send");

        submitButton.setOnAction(event -> {
            String text = inputField.getText();
            if (!text.isEmpty()) {                
                UdpTransmitter.getInstance().send(Main.serverAddress, Main.port, text);
                inputField.clear(); 
            }
        });

        HBox chatSubmit = new HBox();
        chatSubmit.getChildren().addAll(new Label("Chat:"), inputField, submitButton);
        grid.add(chatSubmit, 0, 11); 
    
	}
	public static Chat getInstance()
	{
		if (instance == null) 
			instance = new Chat(10);
		return instance;
	}
	
	public static Chat getInstance(int size)
	{
		if (instance == null) 
			instance = new Chat(size);
		return instance;
	}
	
	public GridPane getGridPane()
	{        
		grid.setHgap(10); // Horizontal spacing between columns
		grid.setVgap(5);  // Vertical spacing between rows

        for (int i = 0; i < 10; i++) {
            
            final int currRow = i;

            grid.getChildren().removeIf(label -> 
            GridPane.getRowIndex(label) != null && 
            GridPane.getRowIndex(label) == currRow
            );
            
            Label chatLbl = new Label(chat[9-i]);
            grid.add(chatLbl, 0, i);

        }        
        return grid;
	}
	
	public void add(String msg)
	{
    	chat[9] = chat[8]; 		// bump the 10th msg off 
    	//shuffle the remaining chats
    	for(int i = 8; i > 0; --i)
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
