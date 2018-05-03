

import javafx.animation.AnimationTimer;
import javafx.animation.Animation;
import javafx.application.Application;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import  javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.scene.paint.Color;
import java.util.ArrayList;
import java.util.List;

import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.VBox;

import static javafx.scene.paint.Color.BLACK;

import javafx.util.Duration;
import javafx.stage.Screen;



public class Main extends Application{
    
    private enum STATUS{
        SPILL,
        MENY
    }
    
    private STATUS Status = STATUS.SPILL;

    private Pane root;
    private Spiller player;
    private VBox stats;
    private VBox pause;
    private VBox meny;
    private Label helse;
    private Label score;
    private Label tid;
    private GameObject wall1;
    private GameObject wall2;
    private GameObject wall3;
    private GameObject wall4;
    private Button strt;
    private Button fortsett;
    private Button lagre;
    private Button quit;
    private AnimationTimer timer;
    private AnimationTimer timer2;
    private boolean powerup;

    private static final Image image = new Image("./bilder/testSprite.png");
    private static final Image playerImage = new Image("./bilder/player.png");
    private static final Image bossImage = new Image("./bilder/bossTank.png");
    private static final Image fienderImage = new Image("./bilder/fiender.png");


    List<Fiender> fiender = new ArrayList<>();
    List<Skudd> bullets = new ArrayList<>();
    List<GameObject> explosions = new ArrayList<>();
    List<GameObject> walls = new ArrayList<>();
    List<FiendeSkudd> Enemybullets = new ArrayList<>();
    List<GameObject> powerups = new ArrayList<>();
    
    public static void Main(String[] args) {

        launch(args);
    }

    


    public boolean equalsX(GameObject gameObject, Point2D point2D){

        return gameObject.getVelocity().getX() == point2D.getX();
    }

    public boolean equalsY(GameObject gameObject, Point2D point2D){

        return gameObject.getVelocity().getY() == point2D.getY();
    }

    
    // Denne metoden Viser og lager alt innenfor Scenen
    private Parent createContent(){
        root = new Pane();
        
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        double width = screenBounds.getWidth();
        double height = screenBounds.getHeight();
        
        root.setPrefSize(height, width);

        player = new Spiller(100, 10, 0,"Gustav",playerAnim(new ImageView(playerImage),0));
        player.setVelocity(new Point2D(0,-0.001));

        // (new Rectangle(20,20,Color.BLUE)),(new Circle(5,5,5,(new Color(0.1f,0.3f,1.0f, 1.0))))

        addGameObject(player, 300, 300);

        wall1 = new GameObject(new Rectangle(width,10,new Color(0, 0, 0, 0.4824)));
        wall2 = new GameObject(new Rectangle(width,10,new Color(0, 0, 0, 0.4824)));
        wall3 = new GameObject(new Rectangle(10,height,new Color(0, 0, 0, 0.4824)));
        wall4 = new GameObject(new Rectangle(10,height,new Color(0, 0, 0, 0.4824)));

        addGameObject(wall1,0,0);
        addGameObject(wall2,0,height - 30);
        addGameObject(wall3,0,0);
        addGameObject(wall4,width - 10,0);

        walls.add(wall1);
        walls.add(wall2);
        walls.add(wall3);
        walls.add(wall4);
        
        helse = new Label();
        score = new Label();
        tid = new Label();
        
        stats = new VBox(10);
        stats.setPadding(new Insets(10, 10, 10, 10));
        stats.setPrefWidth(300);
        stats.setPrefHeight(600);
        stats.getChildren().addAll(helse, score);
        root.getChildren().addAll(stats);

        // timern som får alt til å oppdateres
        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if(Status == STATUS.SPILL){
                    onUpdate();
                }
            }
        };
        timer.start();




       timer2 = new AnimationTimer() {
            @Override
            public void handle(long now) {

                if(Status == STATUS.SPILL){
                   explosionTimer();
                }
            }
        };

        timer2.start();




        return root;
    }
    
    public Parent createMeny(){
        strt = new Button("Start Nytt Spill");
        
        javafx.scene.control.Button hScore = new javafx.scene.control.Button();
        hScore.setText("High Score");
        hScore.setOnAction((ActionEvent event) -> {
            System.out.println("Laster High");
        });
        
        javafx.scene.control.Button load = new javafx.scene.control.Button();
        load.setText("Load");
        load.setOnAction((ActionEvent event) -> {
            System.out.println("Laster"); 
        });
        
        meny = new VBox();
        meny.setPrefSize(400, 300);
        meny.setSpacing(30);
        meny.setAlignment(Pos.CENTER);
        
        meny.getChildren().addAll(strt, hScore, load);
        
        return meny;
    }
    
    public Parent createPause(){
        pause = new VBox(20);
        
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        double width = screenBounds.getWidth();
        double height = screenBounds.getHeight();
        
        pause.setAlignment(Pos.CENTER);
        pause.setPrefWidth(width);
        pause.setPrefHeight(height);
        pause.setBackground(new Background( new BackgroundFill( Color.web( "#000000" ), CornerRadii.EMPTY, Insets.EMPTY ) ));
        
        fortsett = new Button("Fortsett");
        lagre = new Button("Lagre");
        quit = new Button("Avslutt");
        
        pause.getChildren().addAll(fortsett, lagre, quit);
        
        return pause;
    }


    // metode for å legge til og gjøre dem visible
    private void addBullet(Skudd bullet, double x, double y){
        bullets.add(bullet);
        addGameObject(bullet,x,y);
    }


    // Her er en metode for å legge til fiender
    private void addEnemy(Fiender fiende, double x, double y){

        fiender.add(fiende);
        addGameObject(fiende,x,y);

        System.out.println(fiende.getDecideActiveState());
    }

    private void addFiendeBullet(FiendeSkudd bullet, double x, double y){
        Enemybullets.add(bullet);
        addGameObject(bullet,x,y);
    }
    
    private void addPowerUp(GameObject ups, double x, double y){
        powerups.add(ups);
        addGameObject(ups,x,y);

    }

    private void bulletExplosion(GameObject s , double x, double y){
        explosions.add(s);
        addGameObject(s,x,y);

        

    }

    private void explosionTimer(){
        for (GameObject ex : explosions) {

            ex.setAlive(false);
            root.getChildren().remove(ex.getView());


        }
    }



    private Node playAnimation(ImageView w){

        w.setViewport(new Rectangle2D(0,10,10,10));
        final Animation animation = new Sprite(w,Duration.millis(500),2,0,10,10,10);
        animation.setCycleCount(Animation.INDEFINITE);
        animation.play();

        return w;
    }

    public Node playerAnim(ImageView p, int minY){
            //WIP
        p.setViewport(new Rectangle2D(0, minY, 20, 20));
        /*final Animation animation = new Sprite(p,Duration.millis(6000),4,4,0,0,20,20);
        animation.setCycleCount(Animation.INDEFINITE);
        animation.play();

*/
        return p;
    }

    public Node boss(ImageView b, int minY){
        b.setViewport(new Rectangle2D(0, minY, 60, 60));
       // final Animation animation = new Sprite(b,Duration.millis(3000),4,4,0,-16,60,60);
        return b;
    }

    public Node fiender(ImageView b, int minY){
        b.setViewport(new Rectangle2D(0, minY, 20, 20));
        // final Animation animation = new Sprite(b,Duration.millis(3000),4,4,0,-16,60,60);
        return b;
    }


    // GameObject er Parent klassen til alle spill objektene

    public void addGameObject(GameObject object, double x, double y){
        object.getView().setTranslateX(x);
        object.getView().setTranslateY(y);
        root.getChildren().add(object.getView());
    }


    // metoden som oppdaterer spille via en timer i createContent
    private  void onUpdate() {


        helse.setText("HP: " + String.valueOf(player.getHp()));
        score.setText("Score: " + String.valueOf(player.getScore()));

        // Disse Foreach'ene går gjennom Arraylistene våre for å kunne sjekke alle fiender og skudd

        for (Skudd bullet : bullets) {
            for (Fiender fiende : fiender) {
                if (bullet.isColliding(fiende)) {
                    bullet.setAlive(false);
                    fiende.setHp(fiende.getHp() - 34);

                    root.getChildren().remove(bullet.getView());
                    if (fiende.getHp() <= 0) {
                        fiende.setAlive(false);
                        player.setScore();

                        // Tror jeg fucka litt opp litt koden så nå funker den ikke, men den lager litt så mekk java.nio her
                        /*   FileInputStream in;
                        try{
                            in = new FileInputStream(new File("G:\\ProgramUtviklingOppdatert\\BoxHeadTest\\src\\Roblox-Death-Sound-Effect.wav"));
                            AudioStream audio = new AudioStream(in);
                            AudioPlayer.player.start(audio);
                        }catch (Exception e){

                        }*/

                        root.getChildren().remove(fiende.getView());
                    }
                }
            }


            if (bullet.isColliding(wall1) || bullet.isColliding(wall2) || bullet.isColliding(wall3) || bullet.isColliding(wall4)) {

                bulletExplosion((new GameObject(new Circle(10, 10, 10, BLACK))), bullet.getX(), bullet.getY());

                bullet.setAlive(false);
                root.getChildren().remove(bullet.getView());
                //bullet.setVelocity(new Point2D(bullet.getVelocity().getX() + Math.random() * 10 - 5, bullet.getVelocity().getY() + Math.random() * 10 - 5).multiply(-1));


            }


        }






        for (Fiender fiende3 : fiender) {
                for (FiendeSkudd fbullet : Enemybullets) {
                    if (fbullet.isColliding(player)) {
                        player.setHp(player.getHp() - 10);
                        fbullet.setAlive(false);
                        root.getChildren().remove(fbullet.getView());
                        fbullet.update();
                    }
                    if (fbullet.isColliding(wall1) || fbullet.isColliding(wall2) || fbullet.isColliding(wall3) || fbullet.isColliding(wall4)) {

                        if (Math.random() * 13 - 5 < 4) {
                            fbullet.setAlive(false);
                            root.getChildren().remove(fbullet.getView());
                            //fbullet.setVelocity(new Point2D(fbullet.getVelocity().getX() + Math.random() * 10 - 5, fbullet.getVelocity().getY() + Math.random() * 10 - 5).multiply(-1));
                        } else {
                            fbullet.setAlive(false);
                            root.getChildren().remove(fbullet.getView());
                        }
                    }
                }

                if (fiende3.getDecideActiveState() == 0) {

                    if (Math.random() < 0.1) {

                        FiendeSkudd fiendeBullet2 = new FiendeSkudd();
                        addFiendeBullet(fiendeBullet2, fiende3.getView().getTranslateX(), fiende3.getView().getTranslateY());
                        fiendeBullet2.setVelocity((fiende3.getVelocity().normalize().multiply(3)));

                        fiendeBullet2.update();
                    }
                }

                fiende3.FSM(player, fiende3);
                //   fiende3.update();
            }


            for (Fiender fiende1 : fiender) {
                if (fiende1.isColliding(player) || fiende1.isColliding(wall1) || fiende1.isColliding(wall2) || fiende1.isColliding(wall3) || fiende1.isColliding(wall4)) {
                    fiende1.setVelocity(new Point2D(fiende1.getVelocity().getX() + Math.random() * 2 - 1, fiende1.getVelocity().getY() + Math.random() * 2 - 1).multiply(-1));

                    fiende1.update();

                    player.setHp(player.getHp() - 1);
                    if (player.getHp() <= 0) {
                        System.out.println("Game Over");
                    }
                }
            }


            //her fjerner man kuller og fiender hvis de er døde

            Enemybullets.removeIf(FiendeSkudd::isDead);
            bullets.removeIf(Skudd::isDead);
            fiender.removeIf(Fiender::isDead);
            explosions.removeIf(GameObject::isDead);
            bullets.forEach(Skudd::update);
            fiender.forEach(Fiender::update);
            Enemybullets.forEach(FiendeSkudd::update);


            player.update();



            if (player.getScore() >= 32 && player.getScore() <= 33 && fiender.isEmpty()) {
                addEnemy((new Fiender(1000, 10, true, boss(new ImageView(bossImage), 128))), 900, 250 /*Math.random() * 600, Math.random() * 600*/);

            }


            if (fiender.isEmpty()) {
                addEnemy((new Fiender(100, 10, true, fiender((new ImageView(fienderImage)),40))), 300, 250 /*Math.random() * 600, Math.random() * 600*/);
            }

            if (Math.random() < 0.005) {

                addPowerUp(new GameObject(playAnimation(new ImageView(image))), Math.random() * 600, Math.random() * 600);

            }

            for (GameObject x : powerups) {
                if (x.isColliding(player)) {

                    powerup = true;
                    root.getChildren().remove(x.getView());
                }

            }
            //_________________________TESTING OF METHODS_______________________//

            // testing av hp system
            //  System.out.println(player.getHp());
            // System.out.println((player.getView().getTranslateX() - 300)+ "+" + (player.getView().getTranslateY()- 250));

       /*for (Fiender fiende5 : fiender) {
          System.out.println( Math.pow((player.getX() - (fiende5.getX())),2)*0.001);
           System.out.println( Math.pow((player.getY() - (fiende5.getY())),2)*0.001);
        }*/
        }


    @Override
    public void start(Stage stage) throws Exception {
        
        Scene start = new Scene(createMeny());
        
        stage.setScene(start);
        
        // Dette er scenen, vi puter inn createcontent i setScene.
        strt.setOnAction((ActionEvent event) -> {
            stage.setScene(new Scene(createContent()));  
        
            Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();

            stage.setMaximized(true);
            
            Status = STATUS.SPILL;

            stage.getScene().setOnKeyPressed((KeyEvent e) ->{
                if(Status == STATUS.SPILL){

                    if (null != e.getCode()) //dette er bevegelse , Point2D blir brukt dette er en farts vektor og vi har x, -x, y, -y
                    //Keycode er satt opp mot en lambda metode som gjør at når man presser en tast eller releaser den(nedenfor) så gjøres det en handling
                    switch (e.getCode()) {
                        case A:
                            player.setVelocity(new Point2D(-5,0));
                            break;
                        case D:
                            player.setVelocity(new Point2D(5,0));
                            break;
                        case W:
                            player.setVelocity(new Point2D(0,-5));
                            break;
                        case S:
                            player.setVelocity(new Point2D(0,5));
                            break;
                        case Q:
                            player.rotateLeft();
                            break;
                        case E:
                            player.rotateRight();
                            break;
                        default:
                            break;
                    }



                }

                if(e.getCode() == KeyCode.P){
                    if(Status == STATUS.SPILL){
                        Status = STATUS.MENY;
                        root.getChildren().add(createPause());
                        stage.show();
                    }else{
                        Status = STATUS.SPILL;
                        root.getChildren().removeAll(pause);
                    }
                }
                
                fortsett.setOnAction((ActionEvent e1) -> {
                    Status = STATUS.SPILL;
                    root.getChildren().removeAll(pause);
                });
                
                quit.setOnAction((ActionEvent ek) ->{
                    Status = STATUS.MENY;
                    timer.stop();
                    timer2.stop();
                    fiender.clear();
                    bullets.clear();
                    powerup = false;
                    stage.setMaximized(false);
                    stage.setScene(start);
                });

                //_______________________TESTING AV VELOCITY_____________________//
                /*
                System.out.println(player.getVelocity().normalize().multiply(10));
                System.out.println(player.getVelocity());
                System.out.println(player.getVelocity().getX());
                System.out.println(player.getVelocity().getY());
                System.out.println(new Point2D(0,5.0).getY());
                System.out.println(player.getHp()); */
            });

            /*fordi fartsvektoren aldri stopper etter du har presset den , må man lage en keyrelease som setter farten din ned til nærmere 0 verdi
                Det er putta 0.1 som verdi fordi skuddene må vite hvilken retning man står mot, når skuddene blir skutt ut ganges de opp sånn at de går mye raskere.

            */

            stage.getScene().setOnKeyReleased(e ->{
                if(Status == STATUS.SPILL){
                    if (null != e.getCode())switch (e.getCode()) {
                        case A:
                            player.setVelocity(new Point2D(-0.1,0));
                            break;
                        case D:
                            player.setVelocity(new Point2D(0.1,0));
                            break;
                        case W:
                            player.setVelocity(new Point2D(0,-0.1));
                            break;
                        case S:
                            player.setVelocity(new Point2D(0,0.1));
                            break;

                        case SPACE:

                            Skudd bullet = new Skudd(2.5,2.5,2.5,BLACK);


                            if(powerup){
                                if (equalsX(player, new Point2D(0.1,0)) || equalsX(player,new Point2D(-0.1,0))){
                                    Skudd bullet1 = new Skudd(2.5,2.5,2.5,BLACK);
                                    Skudd bullet2 = new Skudd(2.5,2.5,2.5,BLACK);
                                    Skudd bullet3 = new Skudd(2.5,2.5,2.5,BLACK);
                                    Skudd bullet4 = new Skudd(2.5,2.5,2.5,BLACK);
                                    bullet1.setVelocity(player.getVelocity().add(0, 0.01).normalize().multiply(10));
                                    bullet2.setVelocity(player.getVelocity().add(0, 0.02).normalize().multiply(10));
                                    addBullet(bullet1, player.getView().getTranslateX(), player.getView().getTranslateY());
                                    addBullet(bullet2, player.getView().getTranslateX(), player.getView().getTranslateY());
                                    bullet3.setVelocity(player.getVelocity().add(0, -0.01).normalize().multiply(10));
                                    bullet4.setVelocity(player.getVelocity().add(0, -0.02).normalize().multiply(10));
                                    addBullet(bullet3, player.getView().getTranslateX(), player.getView().getTranslateY());
                                    addBullet(bullet4, player.getView().getTranslateX(), player.getView().getTranslateY());

                                } else if (equalsY(player, new Point2D(0,0.1)) || equalsY(player,new Point2D(0,-0.1))){

                                    Skudd bullet1 = new Skudd(2.5,2.5,2.5,BLACK);
                                    Skudd bullet2 = new Skudd(2.5,2.5,2.5,BLACK);
                                    Skudd bullet3 = new Skudd(2.5,2.5,2.5,BLACK);
                                    Skudd bullet4 = new Skudd(2.5,2.5,2.5,BLACK);
                                    bullet1.setVelocity(player.getVelocity().add(0.01, 0).normalize().multiply(10));
                                    bullet2.setVelocity(player.getVelocity().add(0.02, 0).normalize().multiply(10));
                                    addBullet(bullet1, player.getView().getTranslateX(), player.getView().getTranslateY());
                                    addBullet(bullet2, player.getView().getTranslateX(), player.getView().getTranslateY());
                                    bullet3.setVelocity(player.getVelocity().add(-0.01, 0).normalize().multiply(10));
                                    bullet4.setVelocity(player.getVelocity().add(-0.02, 0).normalize().multiply(10));
                                    addBullet(bullet3, player.getView().getTranslateX(), player.getView().getTranslateY());
                                    addBullet(bullet4, player.getView().getTranslateX(), player.getView().getTranslateY());
                                }
                            }

                            bullet.setVelocity(player.getVelocity().normalize().multiply(10));
                            addBullet(bullet, player.getView().getTranslateX(), player.getView().getTranslateY());

                            // Også Powerup
                            if(false){
                                bullet.setVelocity(player.getVelocity().normalize().multiply(30));
                            }
                        break;

                        default:
                            break;
                    }
                }
            });
        });
        stage.show();
    }
}
