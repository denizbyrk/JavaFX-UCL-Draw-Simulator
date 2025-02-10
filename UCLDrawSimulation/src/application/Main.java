package application;
	
import java.util.Map;
import java.util.Random;
import java.util.HashMap;
import java.util.ArrayList;
import java.io.FileReader;
import java.io.BufferedReader;
import javafx.stage.Stage;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.paint.Color;
import javafx.scene.image.Image;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Button;
import javafx.scene.text.FontWeight;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Rectangle;
import javafx.scene.input.MouseEvent;
import javafx.scene.effect.DropShadow;
import javafx.application.Application;

public class Main extends Application {
	
	private class Team {

		public String name;
		public String nation;
		public String abbreviation;
		public int pot;
		public int[] logo = new int[2];
		public ArrayList<Team> pot1Opponents = new ArrayList<Team>();		
		public ArrayList<Team> pot2Opponents = new ArrayList<Team>();
		public ArrayList<Team> pot3Opponents = new ArrayList<Team>();
		public ArrayList<Team> pot4Opponents = new ArrayList<Team>();
		public ArrayList<Team> homeOpponents = new ArrayList<Team>();
		public ArrayList<Team> awayOpponents = new ArrayList<Team>();
		public Team pot1Home, pot1Away, pot2Home, pot2Away, pot3Home, pot3Away, pot4Home, pot4Away;
		public Map<String, Integer> nationCount = new HashMap<>();
		public Map<String, Integer> pot1NationCount = new HashMap<>();
		public Map<String, Integer> pot2NationCount = new HashMap<>();
		public Map<String, Integer> pot3NationCount = new HashMap<>();
		public Map<String, Integer> pot4NationCount = new HashMap<>();
		
		public Team(String name, String nation, String abbreviation, int pot, int l1, int l2) {
			
			this.name = name;
			this.nation = nation;
			this.abbreviation = abbreviation;
			this.pot = pot;
			this.logo[0] = l1;
			this.logo[1] = l2;
		}
		
		public void setOppsNull(Team nullTeam) {
			
			this.pot1Home = nullTeam;
			this.pot1Away = nullTeam;
			this.pot2Home = nullTeam;
			this.pot2Away = nullTeam;
			this.pot3Home = nullTeam;
			this.pot3Away = nullTeam;
			this.pot4Home = nullTeam;
			this.pot4Away = nullTeam;
		}
		
		public void updateCountryCount(Team team) {
	    	
	    	nationCount.put(team.nation, nationCount.getOrDefault(team.nation, 0) + 1);
	    }
	    
		public void updatePot1CountryCount(Team team) {
	    	
	    	pot1NationCount.put(team.nation, pot1NationCount.getOrDefault(team.nation, 0) + 1);
	    }
	    
		public void updatePot2CountryCount(Team team) {
	    	
	    	pot2NationCount.put(team.nation, pot2NationCount.getOrDefault(team.nation, 0) + 1);
	    }
	    
		public void updatePot3CountryCount(Team team) {
	    	
	    	pot3NationCount.put(team.nation, pot3NationCount.getOrDefault(team.nation, 0) + 1);
	    }
	    
		public void updatePot4CountryCount(Team team) {
	    	
	    	pot4NationCount.put(team.nation, pot4NationCount.getOrDefault(team.nation, 0) + 1);
	    }
	}
	
	private boolean drawTeamsDebug = true;
	
	private final String title = "UCL Draw Simulator (2024-2025)";
	private final int screenWidth = 1280;
	private final int screenHeight = 720;
	//loading images
	private final int logoSize = 128;
	private int targetLogoSize = 48;
	private final String textFont = "PT Sans";
	private final Image icon = new Image("Icon.png");
	private Image backgroundImage = new Image("Background.png");
	private Image logoImage = new Image("Logos.png");
	private Image potImage = new Image("UCLPotTable.png");
	private Image fixtureImage = new Image("FixtureTable.png");
	//beginning scene
	private Group root = new Group();
	private Scene firstScene = new Scene(root, this.screenWidth, this.screenHeight, Color.BLUE);
	//team drawing scene
	private Group teamDrawingSceneRoot = new Group();
	private Scene drawingTeamScene = new Scene(teamDrawingSceneRoot, this.screenWidth, this.screenHeight, Color.BLUE);
	//fixture tables scene
	private Group fixtureTablesGroup = new Group();
	private Scene fixtureTablesScene = new Scene(fixtureTablesGroup, this.screenWidth, this.screenHeight, Color.BLUE);
	//storing teams
	private ArrayList<Team> pot1TeamsList = new ArrayList<Team>();
	private ArrayList<Team> pot2TeamsList = new ArrayList<Team>();
	private ArrayList<Team> pot3TeamsList = new ArrayList<Team>();
	private ArrayList<Team> pot4TeamsList = new ArrayList<Team>();
	//create a null team
	private Team nullTeam = new Team("-", "-", "-", -1, -1, -1);
	
	private BufferedReader br;
	private Random random = new Random();
	
	private int displayedFixture = 1;
	private int prevNum;
	private Team chosenTeam;
	private ImageView chosenTeamLogo;
	private Text chosenTeamName;

	private ArrayList<Team> drawnTeams = new ArrayList<Team>();
	private ArrayList<Rectangle> gradients = new ArrayList<Rectangle>();
	
	private boolean textExists = false;
	private Text currentFixture = new Text();
	private ArrayList<Text> fixtureOpponentsName = new ArrayList<Text>();
	private ArrayList<ImageView> fixtureOpponentsLogo = new ArrayList<ImageView>();
	private ArrayList<Text> potTeamsName = new ArrayList<Text>();
	private ArrayList<ImageView> potTeamsLogo = new ArrayList<ImageView>();
	
	public void start(Stage primaryStage) throws Exception {
		
		this.firstScene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		this.drawingTeamScene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		this.fixtureTablesScene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		
		primaryStage.setResizable(false);
		primaryStage.getIcons().add(this.icon);
		primaryStage.setTitle(this.title);
		
		//reading data from CSV file
		String line = "";
		
		this.br = new BufferedReader(new FileReader("src\\teamData.csv"));
		
		//add data into ArrayList
		while ((line = br.readLine()) != null) {
			
			String[] data = line.split(",");
			
			String teamName = data[0];
			String teamNation = data[1];
			String teamAbv = data[5];
			int teamPot = Integer.parseInt(data[2]);
			int[] teamLogo = { Integer.parseInt(data[3]), Integer.parseInt(data[4]) };
			
			Team team = new Team(teamName, teamNation, teamAbv, teamPot, teamLogo[0], teamLogo[1]);
			
			switch(team.pot) {
			
				case 1:
					this.pot1TeamsList.add(team);
					break;
				case 2:
					this.pot2TeamsList.add(team);
					break;
				case 3:
					this.pot3TeamsList.add(team);
					break;
				case 4:
					this.pot4TeamsList.add(team);
					break;
				default:
					break;
			}
		}
		
		for (Team t : this.pot1TeamsList) {
			
			t.setOppsNull(this.nullTeam);
			
			t.homeOpponents.add(t.pot1Home);
			t.homeOpponents.add(t.pot2Home);
			t.homeOpponents.add(t.pot3Home);
			t.homeOpponents.add(t.pot4Home);
			
			t.awayOpponents.add(t.pot1Away);
			t.awayOpponents.add(t.pot2Away);
			t.awayOpponents.add(t.pot3Away);
			t.awayOpponents.add(t.pot4Away);
		}
		
		for (Team t : this.pot2TeamsList) {
			
			t.setOppsNull(this.nullTeam);
			
			t.homeOpponents.add(t.pot1Home);
			t.homeOpponents.add(t.pot2Home);
			t.homeOpponents.add(t.pot3Home);
			t.homeOpponents.add(t.pot4Home);
			
			t.awayOpponents.add(t.pot1Away);
			t.awayOpponents.add(t.pot2Away);
			t.awayOpponents.add(t.pot3Away);
			t.awayOpponents.add(t.pot4Away);
		}
		
		for (Team t : this.pot3TeamsList) {
			
			t.setOppsNull(this.nullTeam);
			
			t.homeOpponents.add(t.pot1Home);
			t.homeOpponents.add(t.pot2Home);
			t.homeOpponents.add(t.pot3Home);
			t.homeOpponents.add(t.pot4Home);
			
			t.awayOpponents.add(t.pot1Away);
			t.awayOpponents.add(t.pot2Away);
			t.awayOpponents.add(t.pot3Away);
			t.awayOpponents.add(t.pot4Away);
		}
		
		for (Team t : this.pot4TeamsList) {
			
			t.setOppsNull(this.nullTeam);
			
			t.homeOpponents.add(t.pot1Home);
			t.homeOpponents.add(t.pot2Home);
			t.homeOpponents.add(t.pot3Home);
			t.homeOpponents.add(t.pot4Home);
			
			t.awayOpponents.add(t.pot1Away);
			t.awayOpponents.add(t.pot2Away);
			t.awayOpponents.add(t.pot3Away);
			t.awayOpponents.add(t.pot4Away);
		}
		
		this.beginningPhase();
		
		primaryStage.setScene(this.firstScene);
		primaryStage.show();
	}
	
	private void beginningPhase() {
		
		this.targetLogoSize = 48;
		
		//background image
		ImageView bgImageView = new ImageView(this.backgroundImage);
		this.root.getChildren().add(bgImageView);
		
		ArrayList<ImageView> potImageViewArrayList = new ArrayList<ImageView>();
		ArrayList<ImageView> pot1LogosArrayList = new ArrayList<ImageView>();
		ArrayList<ImageView> pot2LogosArrayList = new ArrayList<ImageView>();
		ArrayList<ImageView> pot3LogosArrayList = new ArrayList<ImageView>();
		ArrayList<ImageView> pot4LogosArrayList = new ArrayList<ImageView>();
		ArrayList<Text> potTitleTextArrayList = new ArrayList<Text>();
		ArrayList<Text> pot1NamesArrayList = new ArrayList<Text>();
		ArrayList<Text> pot2NamesArrayList = new ArrayList<Text>();
		ArrayList<Text> pot3NamesArrayList = new ArrayList<Text>();
		ArrayList<Text> pot4NamesArrayList = new ArrayList<Text>();
		
		//pot table images
		for (int i = 1; i < 5; i++) {
			
			ImageView potImageView = new ImageView(this.potImage);
			potImageView.setX(64 + (i - 1) * 300);
			potImageView.setY(32);
	        potImageView.setPreserveRatio(true);
	        potImageView.setFitWidth(250);
	        
	        potImageViewArrayList.add(potImageView);
		}
		
		//pot title texts
        for (int i = 1; i < 5; i++) {
        	
        	Text potText = new Text();
        	potText.setFill(Color.WHITE);
    		potText.setFont(Font.font(this.textFont, FontWeight.BOLD, 24));
    		potText.setText("POT " + i);
    		potText.setX(156 + (i - 1) * 300);
    		potText.setY(68);
    		
    		potTitleTextArrayList.add(potText);
        }
        
        for (ImageView iv : potImageViewArrayList) {
        	
        	this.root.getChildren().add(iv);
        }
        
        for (Text t : potTitleTextArrayList) {
        	
        	this.root.getChildren().add(t);
        }

		//logo and team name coordinates
		int x = (int)64 + 3;
		int y = 92;
		
		//load logos and names from pot 1
		for (Team t : this.pot1TeamsList) {
			
			pot1LogosArrayList.add(this.loadTeamLogo(t, x, y));
			pot1NamesArrayList.add(this.loadTeamName(t, x, y, 24));
		
			y += this.targetLogoSize + 9;
		}
		y = 92;
		
		//load logos and names from pot 2
		for (Team t : this.pot2TeamsList) {
			
			pot2LogosArrayList.add(this.loadTeamLogo(t, x + 300, y));
			pot2NamesArrayList.add(this.loadTeamName(t, x + 300, y, 24));
		
			y += this.targetLogoSize + 9;
		}
		y = 92;
		
		//load logos and names from pot 3
		for (Team t : this.pot3TeamsList) {
			
			pot3LogosArrayList.add(this.loadTeamLogo(t, x + 600, y));
			pot3NamesArrayList.add(this.loadTeamName(t, x + 600, y, 24));
		
			y += this.targetLogoSize + 9;
		}
		y = 92;
		
		//load logos and names from pot 4
		for (Team t : this.pot4TeamsList) {
			
			pot4LogosArrayList.add(this.loadTeamLogo(t, x + 900, y));
			pot4NamesArrayList.add(this.loadTeamName(t, x + 900, y, 24));

			y += this.targetLogoSize + 9;
		}
		
		for (ImageView iv : pot1LogosArrayList) {
			
			this.root.getChildren().add(iv);
		}
		
		for (Text t : pot1NamesArrayList) {
			
			this.root.getChildren().add(t);
		}
		
		for (ImageView iv : pot2LogosArrayList) {
			
			this.root.getChildren().add(iv);
		}
		
		for (Text t : pot2NamesArrayList) {
			
			this.root.getChildren().add(t);
		}
		
		for (ImageView iv : pot3LogosArrayList) {
			
			this.root.getChildren().add(iv);
		}
		
		for (Text t : pot3NamesArrayList) {
			
			this.root.getChildren().add(t);
		}
		
		for (ImageView iv : pot4LogosArrayList) {
			
			this.root.getChildren().add(iv);
		}
		
		for (Text t : pot4NamesArrayList) {
			
			this.root.getChildren().add(t);
		}

		//create button for starting program
		Button startBtn = new Button("Begin Drawing");
		startBtn.setPrefSize(240, 48);
		startBtn.setLayoutX(this.screenWidth / 2 - startBtn.getPrefWidth() / 2);
		startBtn.setLayoutY(this.screenHeight - startBtn.getPrefHeight() * 2);
		startBtn.setFont(Font.font(this.textFont, FontWeight.BOLD, 24));
		startBtn.getStyleClass().add("button-regular");
		
		//set action for the button
		startBtn.addEventFilter(MouseEvent.MOUSE_RELEASED, event -> teamDrawingPhase(drawingTeamScene));
		this.root.getChildren().add(startBtn);
	}
	
	//team drawing scene
	private void teamDrawingPhase(Scene scene) {
		
		Stage stage = new Stage();
		this.targetLogoSize = 48;
		
		//create background image
		ImageView bgImageView = new ImageView(this.backgroundImage);
		this.root.getChildren().add(bgImageView);
		 
		//create back button
		Button backBtn = new Button("BACK");
		backBtn.setPrefSize(144, 24);
		backBtn.setLayoutX(16);
		backBtn.setLayoutY(16);
		backBtn.setFont(Font.font(this.textFont, FontWeight.BOLD, 16));
		backBtn.getStyleClass().add("button-regular");
		//add back button event
		backBtn.addEventFilter(MouseEvent.MOUSE_RELEASED, event -> this.beginningPhase());
		this.root.getChildren().add(backBtn);
		
		//create pot table image
		ImageView potImageView = new ImageView(this.potImage);
		potImageView.setX(108);
		potImageView.setY(96);
        potImageView.setPreserveRatio(true);
        potImageView.setFitWidth(250);
        this.root.getChildren().add(potImageView);
        
        //create pot 1 text
    	Text potText = new Text();
    	potText.setFill(Color.WHITE);
		potText.setFont(Font.font(this.textFont, FontWeight.BOLD, 24));
		potText.setText("POT 1");
		potText.setX(potImageView.getX() + 90);
		potText.setY(potImageView.getY() + 36);
		this.root.getChildren().add(potText);
		
		//logo and team name coordinates
		int x = (int)potImageView.getX() + 3;
		int y = (int)potImageView.getY() + 60;
		
		//load logos and names from pot 1
		for (Team t : this.pot1TeamsList) {
			
			this.root.getChildren().add(this.loadTeamLogo(t, x, y));
			this.root.getChildren().add(this.loadTeamName(t, x, y, 24));
		
			y += this.targetLogoSize + 9;
		}
		
		//create chosen team table
		Image newPotImage = new Image("UCLPotTable.png", 250, 567.6923076923078, false, false);
		ImageView chosenTeamImageView = new ImageView(newPotImage);
		chosenTeamImageView.setX(this.screenWidth / 2 - newPotImage.getWidth() / 2);
		chosenTeamImageView.setY(this.screenHeight / 2 - 112);
        Rectangle2D areaToShow = new Rectangle2D(0, 0, 250, 112);
        chosenTeamImageView.setViewport(areaToShow);
        this.root.getChildren().add(chosenTeamImageView);
        
        //create chosen team text
    	Text chosenTeamText = new Text();
    	chosenTeamText.setFill(Color.WHITE);
    	chosenTeamText.setFont(Font.font(this.textFont, FontWeight.BOLD, 24));
    	chosenTeamText.setText("Chosen Team");
    	chosenTeamText.setX(chosenTeamImageView.getX() + 60);
    	chosenTeamText.setY(chosenTeamImageView.getY() + 36);
		this.root.getChildren().add(chosenTeamText);
        
		//create get random team button
		Button getRandomTeamButton = new Button("Get Random Team");
		getRandomTeamButton.setPrefSize(250, 48);
		getRandomTeamButton.setLayoutX(chosenTeamImageView.getX());
		getRandomTeamButton.setLayoutY(chosenTeamImageView.getY() + 144);
		getRandomTeamButton.setFont(Font.font(this.textFont, FontWeight.BOLD, 24));
		getRandomTeamButton.getStyleClass().add("button-regular");
		
		//create clear team button
		Button clearTeamButton = new Button("X");
		clearTeamButton.setPrefSize(20, 48);
		clearTeamButton.setLayoutX(chosenTeamImageView.getX() - 60);
		clearTeamButton.setLayoutY(chosenTeamImageView.getY() + 58);
		clearTeamButton.setFont(Font.font(this.textFont, FontWeight.BOLD, 24));
		clearTeamButton.getStyleClass().add("button-cancel");
		clearTeamButton.setVisible(false);
		
		//create draw opponents button
		Button drawOpponentsButton = new Button("Draw Opponents");
		drawOpponentsButton.setPrefSize(250, 48);
		drawOpponentsButton.setLayoutX(chosenTeamImageView.getX());
		drawOpponentsButton.setLayoutY(chosenTeamImageView.getY() + 216);
		drawOpponentsButton.setFont(Font.font(this.textFont, FontWeight.BOLD, 24));
		drawOpponentsButton.getStyleClass().add("button-regular");
		drawOpponentsButton.setDisable(true);
		
		//create opponent table image
		ImageView opponentTableImageView = new ImageView(newPotImage);
		opponentTableImageView.setX(this.screenWidth - opponentTableImageView.getFitWidth() - 356);
		opponentTableImageView.setY(128);
        Rectangle2D areaToShow2 = new Rectangle2D(0, 0, 250, 512);
        opponentTableImageView.setViewport(areaToShow2);
        this.root.getChildren().add(opponentTableImageView);
        
        //create opponents text
    	Text opponentsText = new Text();
    	opponentsText.setFill(Color.WHITE);
    	opponentsText.setFont(Font.font(this.textFont, FontWeight.BOLD, 24));
    	opponentsText.setText("OPPONENTS");
    	opponentsText.setX(opponentTableImageView.getX() + 60);
    	opponentsText.setY(opponentTableImageView.getY() + 36);
		this.root.getChildren().add(opponentsText);
		
		int homeY = 185;
		int homeY2 = 190;
		Color c;
		//create background boxes for home/away texts
		for (int i = 0; i < 8; i++) {
			
			c = (i % 2 == 0) ? Color.rgb(14, 58, 251, 0.5) : Color.rgb(1, 28, 91, 0.5);
			
			Rectangle homeBox = new Rectangle();
			homeBox.setFill(c);
			homeBox.setWidth(53.846153846153854);
			homeBox.setHeight(56.15384615);
			homeBox.setX(opponentTableImageView.getX() + 250);
			homeBox.setY(homeY);
			
			homeY += 57;
			
	        this.root.getChildren().add(homeBox);
		}
		//create "h" for home and "a" for away
		for (int i = 0; i < 8; i++) {
			
			String text = "";
			
			text = (i % 2 == 0) ? "H" : "A";
			
			Text haText = new Text();
			haText.setFill(Color.WHITE);
			haText.setFont(Font.font(this.textFont, FontWeight.BOLD, 32));
			haText.setText(text);
			haText.setX(opponentTableImageView.getX() + 266);
			haText.setY(homeY2 + 34);
			
			homeY2 += 57;
			
			this.root.getChildren().add(haText);
		}
				
		//add event to get random team button
		getRandomTeamButton.addEventFilter(MouseEvent.MOUSE_RELEASED, event -> {
			
			int selectedTeam = this.random.nextInt(0, this.pot1TeamsList.size());
			this.chosenTeam = this.pot1TeamsList.get(selectedTeam);
			
			while ((this.pot1TeamsList.size() > 0 && selectedTeam == this.prevNum) ||
					(this.drawnTeams.size() > 0 && this.drawnTeams.contains(this.chosenTeam))) {
				
				selectedTeam = this.random.nextInt(0, this.pot1TeamsList.size());
				this.chosenTeam = this.pot1TeamsList.get(selectedTeam);
			}
			
			this.chosenTeamLogo = this.loadTeamLogo(this.pot1TeamsList.get(selectedTeam), (int)chosenTeamImageView.getX() + 3, (int)chosenTeamImageView.getY() + 60);
			this.chosenTeamName = this.loadTeamName(this.pot1TeamsList.get(selectedTeam), (int)chosenTeamImageView.getX() + 3, (int)chosenTeamImageView.getY() + 60, 24);
			
			this.root.getChildren().add(this.chosenTeamLogo);
			this.root.getChildren().add(this.chosenTeamName);
			
			getRandomTeamButton.setDisable(true);
			clearTeamButton.setVisible(true);
			drawOpponentsButton.setDisable(false);
			
			this.prevNum = selectedTeam;
		});
		this.root.getChildren().add(getRandomTeamButton);
		
		ArrayList<Text> opponentNames = new ArrayList<Text>();
		ArrayList<ImageView> opponentLogos = new ArrayList<ImageView>();
		
		clearTeamButton.addEventFilter(MouseEvent.MOUSE_RELEASED, event -> {

			this.root.getChildren().remove(this.chosenTeamName);
			this.root.getChildren().remove(this.chosenTeamLogo);
			
			for (ImageView iv : opponentLogos) {
				
				this.root.getChildren().remove(iv);
			}
			opponentLogos.clear();
			
			for (Text t : opponentNames) {
				
				this.root.getChildren().remove(t);
			}
			opponentNames.clear();
			
			clearTeamButton.setVisible(false);
			getRandomTeamButton.setDisable(false);
			drawOpponentsButton.setDisable(true);
		});
		this.root.getChildren().add(clearTeamButton);
		
		int x2 = (int)opponentTableImageView.getX() + 3;
		int y2 = (int)opponentTableImageView.getY() + 60;
		
		drawOpponentsButton.addEventFilter(MouseEvent.MOUSE_RELEASED, event -> {
			
			this.selectRandomTeam(chosenTeam);
			//temp
			Team team = chosenTeam;
			
			for (int i = 0; i < 8; i++) {
				
				switch(i) {
				
					case 0:
						team = chosenTeam.pot1Home;
						break;
					case 1:
						team = chosenTeam.pot1Away;
						break;
					case 2:
						team = chosenTeam.pot2Home;
						break;
					case 3:
						team = chosenTeam.pot2Away;
						break;
					case 4:
						team = chosenTeam.pot3Home;
						break;
					case 5:
						team = chosenTeam.pot3Away;
						break;
					case 6:
						team = chosenTeam.pot4Home;
						break;
					case 7:
						team = chosenTeam.pot4Away;
						break;
					default:
						break;
				}
				
				opponentLogos.add(this.loadTeamLogo(team, x2, y2 + (9 * i) + (this.targetLogoSize * i)));
				opponentNames.add(this.loadTeamName(team, x2, y2 + (9 * i) + (this.targetLogoSize * i), 24));
			}
			
			for (ImageView iv : opponentLogos) {
				
				this.root.getChildren().add(iv);
			}
			
			for (Text t : opponentNames) {
				
				this.root.getChildren().add(t);
			}
			
			this.gradients.add(this.addGradient(this.getTeamIndex(this.chosenTeam, this.chosenTeam.pot), 108));
			this.loadGradient();
			
			this.drawnTeams.add(this.chosenTeam);
			
			drawOpponentsButton.setDisable(true);
		});
		this.root.getChildren().add(drawOpponentsButton);
		
		//create button for starting program
		Button fixturePhaseButton = new Button("Fixtures");
		fixturePhaseButton.setPrefSize(240, 48);
		fixturePhaseButton.setLayoutX(this.screenWidth / 2 - fixturePhaseButton.getPrefWidth() / 2);
		fixturePhaseButton.setLayoutY(this.screenHeight - fixturePhaseButton.getPrefHeight() * 2);
		fixturePhaseButton.setFont(Font.font(this.textFont, FontWeight.BOLD, 24));
		fixturePhaseButton.getStyleClass().add("button-regular");
		//set action for the button
		fixturePhaseButton.addEventFilter(MouseEvent.MOUSE_RELEASED, event -> {
			
			if (this.gradients.size() > 0) {
				
				for (Rectangle r : this.gradients) {
				
					this.root.getChildren().remove(r);
				}
			}
			
			this.fixtureTables(this.fixtureTablesScene);
		});
		
		this.root.getChildren().add(fixturePhaseButton);
		
		stage.setScene(scene);
	}
	
	private int getTeamIndex(Team chosenTeam, int currentPot) {
		
		ArrayList<Team> pot = new ArrayList<Team>();
		int teamIndex = -1;
		
		switch (currentPot) {
		
			case 1:
				pot = this.pot1TeamsList;
				for (Team t : pot) {
					
					if (t.name.equals(chosenTeam.name)) {
						
						teamIndex = this.pot1TeamsList.indexOf(t);
					}
				}
				break;
			case 2:
				pot = this.pot2TeamsList;
				for (Team t : pot) {
					
					if (t.name.equals(chosenTeam.name)) {
						
						teamIndex = this.pot2TeamsList.indexOf(t);
					}
				}
				break;
			case 3:
				pot = this.pot3TeamsList;
				for (Team t : pot) {
					
					if (t.name.equals(chosenTeam.name)) {
						
						teamIndex = this.pot3TeamsList.indexOf(t);
					}
				}
				break;
			case 4:
				pot = this.pot4TeamsList;
				for (Team t : pot) {
					
					if (t.name.equals(chosenTeam.name)) {
						
						teamIndex = this.pot4TeamsList.indexOf(t);
					}
				}
				break;
		}
		
		return teamIndex;
	}
	
	private void selectRandomTeam(Team team) {
			
		this.getOpponents(team);
		
		System.out.println(team.name + ": " + team.homeOpponents.get(0).name + " | " + team.homeOpponents.get(1).name + " | " + team.homeOpponents.get(2).name + " | " + team.homeOpponents.get(3).name
							+ " | " + team.awayOpponents.get(0).name + " | " + team.awayOpponents.get(1).name + " | " + team.awayOpponents.get(2).name + " | " + team.awayOpponents.get(3).name);
		
		//System.out.println(team.name + ": " + team.pot1Opponents.get(0).name + " | " + team.pot1Opponents.get(1).name + " | " + team.pot2Opponents.get(0).name + " | " + team.pot2Opponents.get(1).name 
			//				+ " | " + team.pot3Opponents.get(0).name + " | " + team.pot3Opponents.get(1).name + " | " + team.pot4Opponents.get(0).name + " | " + team.pot4Opponents.get(1).name);
	}
	
	//get random team
	private int getRandomTeam() {
		
		int randomTeam = this.random.nextInt(0, 9);
		
		return randomTeam;
	}
	
	//draw opponents
	private void getOpponents(Team chosenTeam) {
		
		int iterationCount = chosenTeam.pot1Opponents.size();
		
		if (this.drawTeamsDebug == true) System.out.println(chosenTeam.name + " has " + iterationCount + " opponents in the pot, so it will draw " + (2 - iterationCount) + " opponent(s).");
		//draw pot 1 opponents
		
		for (int i = 0; i < 2; i++) {

			int drawRandomTeam = this.getRandomTeam();
			Team randomTeam = this.pot1TeamsList.get(drawRandomTeam);
			
			if (this.drawTeamsDebug == true) System.out.println(chosenTeam.name + " drew " + randomTeam.name + "!");
			
			if (isValidDraw(chosenTeam, randomTeam, 1) == true) {
				
				chosenTeam.pot1Opponents.add(randomTeam);
				randomTeam.pot1Opponents.add(chosenTeam);
				
				if (i == 0) {
					
					if (chosenTeam.pot1Home.name.equals("-")) {
						
						chosenTeam.pot1Home = randomTeam;
						chosenTeam.homeOpponents.set(0, randomTeam);
					}
					
					if (randomTeam.pot1Away.name.equals("-")) {
						
						randomTeam.pot1Away = chosenTeam;
						randomTeam.awayOpponents.set(0, chosenTeam);
					}
					
				} else if (i == 1) {
						
					if (chosenTeam.pot1Away.name.equals("-")) {
						
						chosenTeam.pot1Away = randomTeam;
						chosenTeam.awayOpponents.set(0, randomTeam);
					}
					
					if (randomTeam.pot1Home.name.equals("-")) {
						
						randomTeam.pot1Home = chosenTeam;
						randomTeam.homeOpponents.set(0, chosenTeam);
					}
				}
				
			} else {
				
				if (chosenTeam.pot1Opponents.size() > 2) {
					
					break;
					
				} else {
					
					i--;
					continue;
				}
			}
		}
		
		int iterationCount2 = chosenTeam.pot2Opponents.size();
		
		if (this.drawTeamsDebug == true) System.out.println(chosenTeam.name + " has " + iterationCount2 + " opponents in the pot, so it will draw " + (2 - iterationCount2) + " opponent(s).");
		//draw pot 2 opponents
		for (int i = 0; i < 2; i++) {
			
			int drawRandomTeam = this.getRandomTeam();
			Team randomTeam = this.pot2TeamsList.get(drawRandomTeam);
			
			if (this.drawTeamsDebug == true) System.out.println(chosenTeam.name + " drew " + randomTeam.name + "!");

			if (isValidDraw(chosenTeam, randomTeam, 2)) {
				
				chosenTeam.pot2Opponents.add(randomTeam);
				randomTeam.pot2Opponents.add(chosenTeam);
				
				if (i == 0) {
					
					if (chosenTeam.pot2Home.name.equals("-")) {
						
						chosenTeam.pot2Home = randomTeam;
						chosenTeam.homeOpponents.set(1, randomTeam);
					}
					
					if (randomTeam.pot2Away.name.equals("-")) {
						
						randomTeam.pot2Away = chosenTeam;
						randomTeam.awayOpponents.set(1, chosenTeam);
					}
					
				} else if (i == 1) {
						
					if (chosenTeam.pot2Away.name.equals("-")) {
						
						chosenTeam.pot2Away = randomTeam;
						chosenTeam.awayOpponents.set(1, randomTeam);
					}
					
					if (randomTeam.pot2Home.name.equals("-")) {
						
						randomTeam.pot2Home = chosenTeam;
						randomTeam.homeOpponents.set(1, chosenTeam);
					}
				}
				
			} else {
				
				if (chosenTeam.pot2Opponents.size() > 2) {
					
					break;
					
				} else {
					
					i--;
					continue;
				}
			}
		}
		
		int iterationCount3 = chosenTeam.pot3Opponents.size();
		
		if (this.drawTeamsDebug == true) System.out.println(chosenTeam.name + " has " + iterationCount3 + " opponents in the pot, so it will draw " + (2 - iterationCount3) + " opponent(s).");
		//draw pot 3 opponents
		for (int i = 0; i < 2; i++) {
			
			int drawRandomTeam = this.getRandomTeam();
			Team randomTeam = this.pot3TeamsList.get(drawRandomTeam);
			
			if (this.drawTeamsDebug == true) System.out.println(chosenTeam.name + " drew " + randomTeam.name + "!");
			
			if (isValidDraw(chosenTeam, randomTeam, 3) == true) {
				
				chosenTeam.pot3Opponents.add(randomTeam);
				randomTeam.pot3Opponents.add(chosenTeam);
				
				if (i == 0) {
					
					if (chosenTeam.pot3Home.name.equals("-")) {
						
						chosenTeam.pot3Home = randomTeam;
						chosenTeam.homeOpponents.set(2, randomTeam);
					}
					
					if (randomTeam.pot3Away.name.equals("-")) {
						
						randomTeam.pot3Away = chosenTeam;
						randomTeam.awayOpponents.set(2, chosenTeam);
					}
					
				} else if (i == 1) {
						
					if (chosenTeam.pot3Away.name.equals("-")) {
						
						chosenTeam.pot3Away = randomTeam;
						chosenTeam.awayOpponents.set(2, randomTeam);
					}
					
					if (randomTeam.pot3Home.name.equals("-")) {
						
						randomTeam.pot3Home = chosenTeam;
						randomTeam.homeOpponents.set(2, chosenTeam);
					}
				}
				
			} else {
				
				if (chosenTeam.pot3Opponents.size() > 2) {
					
					break;
					
				} else {
					
					i--;
					continue;
				}
			}
		}
		
		int iterationCount4 = chosenTeam.pot4Opponents.size();
		
		if (this.drawTeamsDebug == true) System.out.println(chosenTeam.name + " has " + iterationCount4 + " opponents in the pot, so it will draw " + (2 - iterationCount4) + " opponent(s).");
		//draw pot 4 opponents
		for (int i = 0; i < 2; i++) {
			
			int drawRandomTeam = this.getRandomTeam();
			Team randomTeam = this.pot4TeamsList.get(drawRandomTeam);
			
			if (this.drawTeamsDebug == true) System.out.println(chosenTeam.name + " drew " + randomTeam.name + "!");
			
			if (isValidDraw(chosenTeam, randomTeam, 4) == true) {
				
				chosenTeam.pot4Opponents.add(randomTeam);
				randomTeam.pot4Opponents.add(chosenTeam);
				
				if (i == 0) {
					
					if (chosenTeam.pot4Home.name.equals("-")) {
						
						chosenTeam.pot4Home = randomTeam;
						chosenTeam.homeOpponents.set(3, randomTeam);
					}
					
					if (randomTeam.pot4Away.name.equals("-")) {
						
						randomTeam.pot4Away = chosenTeam;
						randomTeam.awayOpponents.set(3, chosenTeam);
					}
					
				} else if (i == 1) {
						
					if (chosenTeam.pot4Away.name.equals("-")) {
						
						chosenTeam.pot4Away = randomTeam;
						chosenTeam.awayOpponents.set(3, randomTeam);
					}
					
					if (randomTeam.pot4Home.name.equals("-")) {
						
						randomTeam.pot4Home = chosenTeam;
						randomTeam.homeOpponents.set(3, chosenTeam);
					}
				}
				
			} else {
				
				if (chosenTeam.pot4Opponents.size() > 2) {
					
					break;
					
				} else {
					
					i--;
					continue;
				}
			}
		}
	}
	
	private boolean isValidDraw(Team chosenTeam, Team drawnTeam, int potToDraw) {
		
		//team cannot draw itself
		if (chosenTeam.name.equals(drawnTeam.name)) {
			
			if (this.drawTeamsDebug == true) System.out.println(chosenTeam.name + " drew itself!");
			return false;
		}
		
		//team cannot draw a team from the same nation as itself
		if (chosenTeam.nation.equals(drawnTeam.nation)) {
			
			if (this.drawTeamsDebug == true) System.out.println(chosenTeam.name + " is from the same nation as " + drawnTeam.name + "!");
			return false;
		}

		switch(potToDraw) {
			
			case 1:
				//check if the chosen team already has a 2 opponents in the pot
				if (chosenTeam.pot1Opponents.size() > 2) {
					
					if (this.drawTeamsDebug == true) System.out.println(chosenTeam.name + " already has 2 opponents!");
					return false;
				}
				//check if the chosen team already drew the same team before
				if (chosenTeam.pot1Opponents.size() > 0 && chosenTeam.pot1Opponents.get(0).equals(drawnTeam)) {
					
					if (this.drawTeamsDebug == true) System.out.println(chosenTeam.name + " has already been drew " + drawnTeam.name + "!");
					return false;
				}
				
				//check if the drawn team already has 2 opponents from the same pot
				if (drawnTeam.pot1Opponents.size() == 2) {
					
					if (this.drawTeamsDebug == true) System.out.println(drawnTeam.name + " already has 2 opponents!");
					return false;
				}
				
				int pot1NationCount = chosenTeam.pot1NationCount.getOrDefault(drawnTeam.nation, 0);
				if (pot1NationCount >= 2) {
					
					if (this.drawTeamsDebug == true) System.out.println(chosenTeam.name + " already drew a team from " + drawnTeam.nation + " in this pot!");
					return false;
				}
				
				/*
				 * if (chosenTeam.pot1Home != this.nullTeam && drawnTeam.pot1Away !=
				 * this.nullTeam) {
				 * 
				 * if (this.drawTeamsDebug == true) System.out.println(chosenTeam.name +
				 * " already has " + chosenTeam.pot1Home.name + " as home opponent. " +
				 * drawnTeam.name + " already has " + drawnTeam.pot1Away.name +
				 * " as away opponent."); return false; }
				 * 
				 * if (chosenTeam.pot1Away != this.nullTeam && drawnTeam.pot1Home !=
				 * this.nullTeam) {
				 * 
				 * if (this.drawTeamsDebug == true) System.out.println(chosenTeam.name +
				 * " already has " + chosenTeam.pot1Away.name + " as away opponent. " +
				 * drawnTeam.name + " already has " + drawnTeam.pot1Home.name +
				 * " as home opponent."); return false; }
				 */
				break;
				
			case 2:
				//check if the chosen team already has a 2 opponents in the pot
				if (chosenTeam.pot2Opponents.size() > 2) {
					
					if (this.drawTeamsDebug == true) System.out.println(chosenTeam.name + " already has 2 opponents!");
					return false;
				}
				//check if the chosen team already drew the same team before
				if (chosenTeam.pot2Opponents.size() > 0 && chosenTeam.pot2Opponents.get(0).equals(drawnTeam)) {
					
					if (this.drawTeamsDebug == true) System.out.println(chosenTeam.name + " has already been drew " + drawnTeam.name + "!");
					return false;
				}
				
				//check if the drawn team already has 2 opponents from the same pot
				if (drawnTeam.pot2Opponents.size() == 2) {
					
					if (this.drawTeamsDebug == true) System.out.println(drawnTeam.name + " already has 2 opponents!");
					return false;
				}
				
				int pot2NationCount = chosenTeam.pot2NationCount.getOrDefault(drawnTeam.nation, 0);
				if (pot2NationCount >= 2) {
					
					if (this.drawTeamsDebug == true) System.out.println(chosenTeam.name + " already drew a team from " + drawnTeam.nation + " in this pot!");
					return false;
				}
				
				/*
				 * if (chosenTeam.pot2Home != this.nullTeam && drawnTeam.pot2Away !=
				 * this.nullTeam) {
				 * 
				 * if (this.drawTeamsDebug == true) System.out.println(chosenTeam.name +
				 * " already has " + chosenTeam.pot2Home.name + " as home opponent. " +
				 * drawnTeam.name + " already has " + drawnTeam.pot2Away.name +
				 * " as away opponent."); return false; }
				 * 
				 * if (chosenTeam.pot2Away != this.nullTeam && drawnTeam.pot2Home !=
				 * this.nullTeam) {
				 * 
				 * if (this.drawTeamsDebug == true) System.out.println(chosenTeam.name +
				 * " already has " + chosenTeam.pot2Away.name + " as away opponent. " +
				 * drawnTeam.name + " already has " + drawnTeam.pot2Home.name +
				 * " as home opponent."); return false; }
				 */
				break;
				
			case 3:
				//check if the chosen team already has a 2 opponents in the pot
				if (chosenTeam.pot3Opponents.size() > 2) {
					
					if (this.drawTeamsDebug == true) System.out.println(chosenTeam.name + " already has 2 opponents!");
					return false;
				}
				//check if the chosen team already drew the same team before
				if (chosenTeam.pot3Opponents.size() > 0 && chosenTeam.pot3Opponents.get(0).equals(drawnTeam)) {
					
					if (this.drawTeamsDebug == true) System.out.println(chosenTeam.name + " has already been drew " + drawnTeam.name + "!");
					return false;
				}
				
				//check if the drawn team already has 2 opponents from the same pot
				if (drawnTeam.pot3Opponents.size() == 2) {
					
					if (this.drawTeamsDebug == true) System.out.println(drawnTeam.name + " already has 2 opponents!");
					return false;
				}
				
				int pot3NationCount = chosenTeam.pot3NationCount.getOrDefault(drawnTeam.nation, 0);
				if (pot3NationCount >= 2) {
					
					if (this.drawTeamsDebug == true) System.out.println(chosenTeam.name + " already drew a team from " + drawnTeam.nation + " in this pot!");
					return false;
				}
				
				/*
				 * if (chosenTeam.pot3Home != this.nullTeam && drawnTeam.pot3Away !=
				 * this.nullTeam) {
				 * 
				 * if (this.drawTeamsDebug == true) System.out.println(chosenTeam.name +
				 * " already has " + chosenTeam.pot3Home.name + " as home opponent. " +
				 * drawnTeam.name + " already has " + drawnTeam.pot1Away.name +
				 * " as away opponent."); return false; }
				 * 
				 * if (chosenTeam.pot3Away != this.nullTeam && drawnTeam.pot3Home !=
				 * this.nullTeam) {
				 * 
				 * if (this.drawTeamsDebug == true) System.out.println(chosenTeam.name +
				 * " already has " + chosenTeam.pot3Away.name + " as away opponent. " +
				 * drawnTeam.name + " already has " + drawnTeam.pot3Home.name +
				 * " as home opponent."); return false; }
				 */
				break;
				
			case 4:
				//check if the chosen team already has a 2 opponents in the pot
				if (chosenTeam.pot4Opponents.size() > 2) {
					
					if (this.drawTeamsDebug == true) System.out.println(chosenTeam.name + " already has 2 opponents!");
					return false;
				}
				//check if the chosen team already drew the same team before
				if (chosenTeam.pot4Opponents.size() > 0 && chosenTeam.pot4Opponents.get(0).equals(drawnTeam)) {
					
					if (this.drawTeamsDebug == true) System.out.println(chosenTeam.name + " has already been drew " + drawnTeam.name + "!");
					return false;
				}
				
				//check if the drawn team already has 2 opponents from the same pot
				if (drawnTeam.pot4Opponents.size() == 2) {
					
					if (this.drawTeamsDebug == true) System.out.println(drawnTeam.name + " already has 2 opponents!");
					return false;
				}
				
				int pot4NationCount = chosenTeam.pot4NationCount.getOrDefault(drawnTeam.nation, 0);
				if (pot4NationCount >= 2) {
					
					if (this.drawTeamsDebug == true) System.out.println(chosenTeam.name + " already drew a team from " + drawnTeam.nation + " in this pot!");
					return false;
				}
				
				/*
				 * if (chosenTeam.pot4Home != this.nullTeam && drawnTeam.pot4Away !=
				 * this.nullTeam) {
				 * 
				 * if (this.drawTeamsDebug == true) System.out.println(chosenTeam.name +
				 * " already has " + chosenTeam.pot4Home.name + " as home opponent. " +
				 * drawnTeam.name + " already has " + drawnTeam.pot4Away.name +
				 * " as away opponent."); return false; }
				 * 
				 * if (chosenTeam.pot4Away != this.nullTeam && drawnTeam.pot4Home !=
				 * this.nullTeam) {
				 * 
				 * if (this.drawTeamsDebug == true) System.out.println(chosenTeam.name +
				 * " already has " + chosenTeam.pot4Away.name + " as away opponent. " +
				 * drawnTeam.name + " already has " + drawnTeam.pot1Home.name +
				 * " as home opponent."); return false; }
				 */
				break;
				
			default:
				if (this.drawTeamsDebug == true) System.out.println("Invalid Pot");
				break;
		}
		
		//check if the chosen team draws more than 2 teams from a nation
		int sameNationCount = chosenTeam.nationCount.getOrDefault(drawnTeam.nation, 0);
		if (sameNationCount >= 2) {
			
			if (this.drawTeamsDebug == true) System.out.println(chosenTeam.name + " already drew 2 teams from " + drawnTeam.nation);
			return false;
		}
		
		chosenTeam.updateCountryCount(drawnTeam);
		chosenTeam.updatePot1CountryCount(drawnTeam);
		chosenTeam.updatePot2CountryCount(drawnTeam);
		chosenTeam.updatePot3CountryCount(drawnTeam);
		chosenTeam.updatePot4CountryCount(drawnTeam);
		
		return true;
	}
	
	private Rectangle addGradient(int team, int x) {
		
		int y;
		
		switch(team) {
		
			case 0:
				y = 153;
				break;
			case 1:
				y = 210;
				break;
			case 2:
				y = 267;
				break;
			case 3:
				y = 324;
				break;
			case 4:
				y = 381;
				break;
			case 5:
				y = 438;
				break;
			case 6:
				y = 495;
				break;
			case 7:
				y = 552;
				break;
			case 8:
				y = 609;
				break;
			default:
				y = 0;
				break;
		}
		
		Rectangle rect = new Rectangle(x, y, 250, 55);
		rect.setOpacity(0.5);
		rect.setFill(Color.DARKBLUE);
		
		return rect;
	}
	
	private void loadGradient() {
		
		if (this.gradients.size() > 0) {
			
			for (Rectangle r : this.gradients) {
				
				if (this.root.getChildren().contains(r) == false) {
					
					this.root.getChildren().add(r);	
				}
			}
		} else {
			
			this.root.getChildren().add(this.gradients.get(0));
		}
	}
	
	//method for displaying team name
	private Text loadTeamName(Team t, int x, int y, int fontSize) {
		
		Text teamName = new Text();
		
		teamName.setFill(Color.WHITE);
		teamName.setFont(Font.font(this.textFont, FontWeight.LIGHT, fontSize));
		teamName.setText(t.name);
		teamName.setX(x + 56);
		teamName.setY(y + 32);
		
		return teamName;
	}
	
	//method for displaying team name
	private Text loadTeamAbbv(Team t, int x, int y, int fontSize) {
		
		Text teamName = new Text();
		
		teamName.setFill(Color.WHITE);
		teamName.setFont(Font.font(this.textFont, FontWeight.LIGHT, fontSize));
		teamName.setText(t.abbreviation);
		teamName.setX(x + 56);
		teamName.setY(y + 32);
		
		return teamName;
	}
	
	//method for displaying team logo
	private ImageView loadTeamLogo(Team t, int x, int y) {
		
		ImageView imageView = new ImageView(this.logoImage);
		
		imageView.setX(x);
		imageView.setY(y);
		
		//rectangle for cropping logo
        Rectangle2D logoToShow = new Rectangle2D(t.logo[0] * this.logoSize, t.logo[1] * this.logoSize, this.logoSize, this.logoSize);
        imageView.setViewport(logoToShow);
        
        imageView.setPreserveRatio(true);
        imageView.setFitWidth(this.targetLogoSize);
        
        return imageView;
	}
	
	private void fixtureTables(Scene scene) {
		
		Stage stage = new Stage();
		
		//create background image
		ImageView bgImageView = new ImageView(this.backgroundImage);
		this.root.getChildren().add(bgImageView);
		
		//create back button
		Button backBtn = new Button("BACK");
		backBtn.setPrefSize(144, 24);
		backBtn.setLayoutX(16);
		backBtn.setLayoutY(16);
		backBtn.setFont(Font.font(this.textFont, FontWeight.BOLD, 16));
		backBtn.getStyleClass().add("button-regular");
		//add back button event
		backBtn.addEventFilter(MouseEvent.MOUSE_RELEASED, event -> {
			
			this.textExists = false;
			
			this.teamDrawingPhase(this.drawingTeamScene);
			
			if (this.gradients.size() > 0) this.loadGradient();
		});
		this.root.getChildren().add(backBtn);
		
		ImageView fixtureTableImageView = new ImageView(this.fixtureImage);
		fixtureTableImageView.setX(0);
		fixtureTableImageView.setY(80);
		this.root.getChildren().add(fixtureTableImageView);
		
		this.updateFixture(fixtureTableImageView.getX(), fixtureTableImageView.getY());
		
		Button nextFixtureButton = new Button(">");
		nextFixtureButton.setPrefSize(16, 32);
		nextFixtureButton.setLayoutX(this.screenWidth / 2 + 130);
		nextFixtureButton.setLayoutY(108);
		nextFixtureButton.setFont(Font.font(this.textFont, FontWeight.BOLD, 20));
		nextFixtureButton.getStyleClass().add("button-cancel");
		nextFixtureButton.addEventFilter(MouseEvent.MOUSE_RELEASED, event -> {
						
			this.displayedFixture = (this.displayedFixture == 4) ? 1 : this.displayedFixture + 1;
			
			this.updateFixture(fixtureTableImageView.getX(), fixtureTableImageView.getY());
		});
		this.root.getChildren().add(nextFixtureButton);
		
		Button prevFixtureButton = new Button("<");
		prevFixtureButton.setPrefSize(16, 32);
		prevFixtureButton.setLayoutX(this.screenWidth / 2 - 176);
		prevFixtureButton.setLayoutY(108);
		prevFixtureButton.setFont(Font.font(this.textFont, FontWeight.BOLD, 20));
		prevFixtureButton.getStyleClass().add("button-cancel");
		prevFixtureButton.addEventFilter(MouseEvent.MOUSE_RELEASED, event -> {
						
			this.displayedFixture = (this.displayedFixture == 1) ? 4 : this.displayedFixture - 1;
			
			this.updateFixture(fixtureTableImageView.getX(), fixtureTableImageView.getY());
		});
		this.root.getChildren().add(prevFixtureButton);
		
		//create save button
		Button saveButton = new Button("Save");
		saveButton.setPrefSize(240, 48);
		saveButton.setLayoutX(this.screenWidth - saveButton.getPrefWidth() - 64);
		saveButton.setLayoutY(this.screenHeight - saveButton.getPrefHeight() - 64);
		saveButton.setFont(Font.font(this.textFont, FontWeight.BOLD, 24));
		saveButton.getStyleClass().add("button-regular");
		//set action for the button
		//saveButton.addEventFilter(MouseEvent.MOUSE_RELEASED, event -> this.fixtureTables(this.fixtureTablesScene));
		
		this.root.getChildren().add(saveButton);
		
		stage.setScene(scene);
	}
	
	private void updateFixture(double fixtureTableX, double fixtureTableY) {
		
		this.root.getChildren().remove(this.currentFixture);
		
		DropShadow dropShadow = new DropShadow();	
		dropShadow.setRadius(5.0);
		dropShadow.setSpread(0.2);
		dropShadow.setOffsetX(0);
		dropShadow.setOffsetY(0);
		dropShadow.setColor(Color.WHITE);
		
		this.currentFixture.setFill(Color.WHITE);
		this.currentFixture.setFont(Font.font(this.textFont, FontWeight.BOLD, 32));
		this.currentFixture.setX(this.screenWidth / 2 - 116);
		this.currentFixture.setY(fixtureTableY + 64);
		this.currentFixture.setEffect(dropShadow);
		this.root.getChildren().add(this.currentFixture);
		
		for (Text t : this.fixtureOpponentsName) {
			
			this.root.getChildren().remove(t);
		}
		
		for (ImageView iv : this.fixtureOpponentsLogo) {
			
			this.root.getChildren().remove(iv);
		}
		
		for (Text t : this.potTeamsName) {
			
			this.root.getChildren().remove(t);
		}
		
		for (ImageView iv : this.potTeamsLogo) {
			
			this.root.getChildren().remove(iv);
		}
		
		this.fixtureOpponentsName.clear();
		this.fixtureOpponentsLogo.clear();
		this.potTeamsName.clear();
		this.potTeamsLogo.clear();
		
		switch(this.displayedFixture) {
		
			case 1:
				this.currentFixture.setText("POT 1 FIXTURES");
				break;
			case 2:
				this.currentFixture.setText("POT 2 FIXTURES");
				break;
			case 3:
				this.currentFixture.setText("POT 3 FIXTURES");
				break;
			case 4:
				this.currentFixture.setText("POT 4 FIXTURES");
				break;
			default:
				break;
		}
		
		int x = (int)fixtureTableX+ 356;
		int x2 = (int)fixtureTableX + 460;
		int y = (int)fixtureTableY + 132;
		
		if (this.textExists == false) {
			
			for (int i = 0; i < 4; i++) {
				
		    	Text homeText = new Text();
		    	homeText.setFill(Color.WHITE);
		    	homeText.setFont(Font.font(this.textFont, FontWeight.NORMAL, 16));
		    	homeText.setText("HOME");
		    	homeText.setX(x + (i * 240));
		    	homeText.setY(y);
		    	
				this.root.getChildren().add(homeText);
				
		    	Text awayText = new Text();
		    	awayText.setFill(Color.WHITE);
		    	awayText.setFont(Font.font(this.textFont, FontWeight.NORMAL, 16));
		    	awayText.setText("AWAY");
		    	awayText.setX(x2 + (i * 240));
		    	awayText.setY(y);
		    	
				this.root.getChildren().add(awayText);
			}
			
			for (int i = 1; i < 5; i++) {
				
		    	Text potText = new Text();
		    	potText.setFill(Color.WHITE);
		    	potText.setFont(Font.font(this.textFont, FontWeight.NORMAL, 16));
		    	potText.setText("POT " + i);
		    	potText.setX(x - 187 + (i * 240));
		    	potText.setY(y - 32);
		    	
		    	this.root.getChildren().add(potText);
			}
		}
		
		this.textExists = true;
		
		switch (this.displayedFixture) {
		
			case 1:
				
				int potTeamsX = (int)fixtureTableX + 128;
				int potTeamsY = (int)fixtureTableY + 172;
				
				for (Team t : this.pot1TeamsList) {
					
					this.targetLogoSize = 24;
					
					this.potTeamsLogo.add(this.loadTeamLogo(t, potTeamsX, potTeamsY));
					this.potTeamsName.add(this.loadTeamName(t, potTeamsX - 16, potTeamsY - 16, 16));
					
					potTeamsY += this.targetLogoSize + 9;
				}
				
				for (Team t : this.pot1TeamsList) {
					
					int y2 = (int)fixtureTableY + (this.getTeamIndex(t, t.pot) * 33) + 160;
					
					for (int i = 0; i < 4 ; i++) {
						
						if (t.homeOpponents.get(i).name.equals("-")) {
							
							this.fixtureOpponentsName.add(this.loadTeamName(t.homeOpponents.get(i), (int)fixtureTableX + 308 + (i  * 240), y2, 24));
						} else {
							
							this.fixtureOpponentsName.add(this.loadTeamAbbv(t.homeOpponents.get(i), (int)fixtureTableX + 305 + (i  * 240), y2 - 2, 16));
							this.fixtureOpponentsLogo.add(this.loadTeamLogo(t.homeOpponents.get(i), (int)fixtureTableX + 327 + (i  * 240), y2 + 12));
						}
					}
					
					for (int i = 0; i < 4 ; i++) {
						
						if (t.awayOpponents.get(i).name.equals("-")) {
							
							this.fixtureOpponentsName.add(this.loadTeamName(t.awayOpponents.get(i), (int)fixtureTableX + 429 + (i  * 240), y2, 24));
						} else {
							
							this.fixtureOpponentsName.add(this.loadTeamAbbv(t.awayOpponents.get(i), (int)fixtureTableX + 425 + (i  * 240), y2 - 2, 16));
							this.fixtureOpponentsLogo.add(this.loadTeamLogo(t.awayOpponents.get(i), (int)fixtureTableX + 449 + (i  * 240), y2 + 12));
						}
					}
				}
				
				break;
			case 2:
				
				int potTeamsX2 = (int)fixtureTableX + 128;
				int potTeamsY2 = (int)fixtureTableY + 172;
				
				for (Team t : this.pot2TeamsList) {
					
					this.targetLogoSize = 24;
					
					this.potTeamsLogo.add(this.loadTeamLogo(t, potTeamsX2, potTeamsY2));
					this.potTeamsName.add(this.loadTeamName(t, potTeamsX2 - 16, potTeamsY2 - 16, 16));
					
					potTeamsY2 += this.targetLogoSize + 9;
				}
				
				for (Team t : this.pot2TeamsList) {
					
					int y2 = (int)fixtureTableY + (this.getTeamIndex(t, t.pot) * 33) + 160;
					
					for (int i = 0; i < 4 ; i++) {
						
						if (t.homeOpponents.get(i).name.equals("-")) {
							
							this.fixtureOpponentsName.add(this.loadTeamName(t.homeOpponents.get(i), (int)fixtureTableX + 308 + (i  * 240), y2, 24));
						} else {
							
							this.fixtureOpponentsName.add(this.loadTeamAbbv(t.homeOpponents.get(i), (int)fixtureTableX + 306 + (i  * 240), y2 - 2, 16));
							this.fixtureOpponentsLogo.add(this.loadTeamLogo(t.homeOpponents.get(i), (int)fixtureTableX + 327 + (i  * 240), y2 + 12));
						}
					}
					
					for (int i = 0; i < 4 ; i++) {
						
						if (t.awayOpponents.get(i).name.equals("-")) {
							
							this.fixtureOpponentsName.add(this.loadTeamName(t.awayOpponents.get(i), (int)fixtureTableX + 429 + (i  * 240), y2, 24));
						} else {
							
							this.fixtureOpponentsName.add(this.loadTeamAbbv(t.awayOpponents.get(i), (int)fixtureTableX + 425 + (i  * 240), y2 - 2, 16));
							this.fixtureOpponentsLogo.add(this.loadTeamLogo(t.awayOpponents.get(i), (int)fixtureTableX + 449 + (i  * 240), y2 + 12));
						}
					}
				}
				
				break;
			case 3:
				
				int potTeamsX3 = (int)fixtureTableX + 128;
				int potTeamsY3 = (int)fixtureTableY + 172;
				
				for (Team t : this.pot3TeamsList) {
					
					this.targetLogoSize = 24;
					
					this.potTeamsLogo.add(this.loadTeamLogo(t, potTeamsX3, potTeamsY3));
					this.potTeamsName.add(this.loadTeamName(t, potTeamsX3 - 16, potTeamsY3 - 16, 16));
					
					potTeamsY3 += this.targetLogoSize + 9;
				}
				
				for (Team t : this.pot3TeamsList) {
					
					int y2 = (int)fixtureTableY + (this.getTeamIndex(t, t.pot) * 33) + 160;
					
					for (int i = 0; i < 4 ; i++) {
						
						if (t.homeOpponents.get(i).name.equals("-")) {
							
							this.fixtureOpponentsName.add(this.loadTeamName(t.homeOpponents.get(i), (int)fixtureTableX + 308 + (i  * 240), y2, 24));
						} else {
							
							this.fixtureOpponentsName.add(this.loadTeamAbbv(t.homeOpponents.get(i), (int)fixtureTableX + 306 + (i  * 240), y2 - 2, 16));
							this.fixtureOpponentsLogo.add(this.loadTeamLogo(t.homeOpponents.get(i), (int)fixtureTableX + 327 + (i  * 240), y2 + 12));
						}
					}
					
					for (int i = 0; i < 4 ; i++) {
						
						if (t.awayOpponents.get(i).name.equals("-")) {
							
							this.fixtureOpponentsName.add(this.loadTeamName(t.awayOpponents.get(i), (int)fixtureTableX + 429 + (i  * 240), y2, 24));
						} else {
							
							this.fixtureOpponentsName.add(this.loadTeamAbbv(t.awayOpponents.get(i), (int)fixtureTableX + 425 + (i  * 240), y2 - 2, 16));
							this.fixtureOpponentsLogo.add(this.loadTeamLogo(t.awayOpponents.get(i), (int)fixtureTableX + 449 + (i  * 240), y2 + 12));
						}
					}
				}
				
				break;
			case 4:
				
				int potTeamsX4 = (int)fixtureTableX + 128;
				int potTeamsY4 = (int)fixtureTableY + 172;
				
				for (Team t : this.pot4TeamsList) {
					
					this.targetLogoSize = 24;
					
					this.potTeamsLogo.add(this.loadTeamLogo(t, potTeamsX4, potTeamsY4));
					this.potTeamsName.add(this.loadTeamName(t, potTeamsX4 - 16, potTeamsY4 - 16, 16));
					
					potTeamsY4 += this.targetLogoSize + 9;
				}
				
				for (Team t : this.pot4TeamsList) {
					
					int y2 = (int)fixtureTableY + (this.getTeamIndex(t, t.pot) * 33) + 160;
					
					for (int i = 0; i < 4 ; i++) {
						
						if (t.homeOpponents.get(i).name.equals("-")) {
							
							this.fixtureOpponentsName.add(this.loadTeamName(t.homeOpponents.get(i), (int)fixtureTableX + 308 + (i  * 240), y2, 24));
						} else {
							
							this.fixtureOpponentsName.add(this.loadTeamAbbv(t.homeOpponents.get(i), (int)fixtureTableX + 306 + (i  * 240), y2 - 2, 16));
							this.fixtureOpponentsLogo.add(this.loadTeamLogo(t.homeOpponents.get(i), (int)fixtureTableX + 327 + (i  * 240), y2 + 12));
						}
					}
					
					for (int i = 0; i < 4 ; i++) {
						
						if (t.awayOpponents.get(i).name.equals("-")) {
							
							this.fixtureOpponentsName.add(this.loadTeamName(t.awayOpponents.get(i), (int)fixtureTableX + 429 + (i  * 240), y2, 24));
						} else {
							
							this.fixtureOpponentsName.add(this.loadTeamAbbv(t.awayOpponents.get(i), (int)fixtureTableX + 425 + (i  * 240), y2 - 2, 16));
							this.fixtureOpponentsLogo.add(this.loadTeamLogo(t.awayOpponents.get(i), (int)fixtureTableX + 449 + (i  * 240), y2 + 12));
						}
					}
				}
				
				break;
		}
		
		for (Text t : this.fixtureOpponentsName) {
		
			this.root.getChildren().add(t);
		}
		
		for (ImageView iv : this.fixtureOpponentsLogo) {
			
			this.root.getChildren().add(iv);
		}
		
		for (Text t : this.potTeamsName) {
			
			this.root.getChildren().add(t);
		}
		
		for (ImageView iv : this.potTeamsLogo) {
			
			this.root.getChildren().add(iv);
		}
	}
	
	//main method
	public static void main(String[] args) {
		
		Application.launch(args);
	}
}