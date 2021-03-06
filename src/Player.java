import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

public class Player implements GameObject {
  private Rectangle playerRectangel;
  private int speed = 7;
  private Sprite sprite;
  private AnimatedSprite animatedSprite = null;
  //0-down, 1-left, 2-right, 3-up
  private int direction = 0;
  private Game game;
  
  public Player(Sprite sprite, Game game) {
    this.sprite = sprite;
    this.game = game;
    
    if (sprite instanceof AnimatedSprite) {
      animatedSprite = (AnimatedSprite) sprite;
    }
    updateDirection();
    playerRectangel = new Rectangle(32, 16, 20, 20);
    playerRectangel.generateGraphics(3, 0xFF0000);
  }
  
  private void updateDirection() {
    if (animatedSprite != null) {
      // 8-is the number of sprites right now in one moving animation
      animatedSprite.setAnimationRange(direction * 4, direction * 4 + 3);
    }
  }
  
  //call every time physicaly possible
  public void render(RenderHandler renderer, int xZoom, int yZoom) {
    if (animatedSprite != null) {
      renderer.renderSprite(animatedSprite, playerRectangel.x - playerRectangel.w / 2, playerRectangel.y - playerRectangel.h * 3, xZoom, yZoom, false);
    } else if (sprite != null) {
      renderer.renderSprite(sprite, playerRectangel.x, playerRectangel.y, xZoom, yZoom, false);
    } else {
      renderer.renderRectangle(playerRectangel, xZoom, yZoom, false);
    }
    //Hitbox rendering
    //renderer.renderRectangle(playerRectangel, xZoom, yZoom, false);
  }
  
  //call whatever mouse is clicked on Canvas
  public boolean handleMouseClick(Rectangle mouseRectangle, Rectangle camera, int xZoom, int yZoom) {
    return false;
  }
  
  //call at 60 fps rate
  public void update(Game game) {
    KeyBoardListener keyListener = game.getKeyListener();
    boolean didMoved = false;
    int newDirection = direction;
    Rectangle newPlayerPos = new Rectangle(playerRectangel.x, playerRectangel.y, playerRectangel.w, playerRectangel.h);
    
    if (keyListener.left()) {
      newPlayerPos.x -= speed;
      didMoved = true;
      newDirection = 1;
      if (!checkCollision(newPlayerPos)) {
        playerRectangel.x = newPlayerPos.x;
        playerRectangel.y = newPlayerPos.y;
      }
    }
    if (keyListener.right()) {
      newPlayerPos.x += speed;
      didMoved = true;
      newDirection = 2;
      if (!checkCollision(newPlayerPos)) {
        playerRectangel.x = newPlayerPos.x;
        playerRectangel.y = newPlayerPos.y;
      }
    }
    if (keyListener.up()) {
      newPlayerPos.y -= speed;
      didMoved = true;
      newDirection = 3;
      if (!checkCollision(newPlayerPos)) {
        playerRectangel.x = newPlayerPos.x;
        playerRectangel.y = newPlayerPos.y;
      }
    }
    if (keyListener.down()) {
      newPlayerPos.y += speed;
      didMoved = true;
      newDirection = 0;
      if (!checkCollision(newPlayerPos)) {
        playerRectangel.x = newPlayerPos.x;
        playerRectangel.y = newPlayerPos.y;
      }
    }
    
    if (newDirection != direction) {
      direction = newDirection;
      updateDirection();
    }
    uptadeCamera(game.getRenderer().getCamera());
    if (didMoved) {
      animatedSprite.update(game);
    } else {
      animatedSprite.reset();
    }
  }
  
  public void uptadeCamera(Rectangle camera) {
    camera.x = playerRectangel.x - (camera.w / 2);
    camera.y = playerRectangel.y - (camera.h / 2);
  }
  
  private boolean checkCollision(Rectangle newPlayerPos) {
    ConcurrentSkipListMap<Integer, List<MapObject>> mapObjects = game.getMapObjects();
    int playerYpos = Math.floorDiv((playerRectangel.y + playerRectangel.h), game.getyZoom() * game.getTileSize());
    
    if (mapObjects.size() < 1) {
      return false;
    }
    for (Map.Entry<Integer, List<MapObject>> entry : mapObjects.entrySet()) {
      List<MapObject> value = entry.getValue();
      int yPosObjects = entry.getKey();
      //checks if player y is close to objects y, if not, skips them.
      if (3 < (Math.abs(yPosObjects - playerYpos)))
        continue;
      
      for (int i = 0; i < value.size(); i++) {
        if (newPlayerPos.isOverlaping(value.get(i).getHitBox(), game.getxZoom(), game.getyZoom())) {
          return true;
        }
      }
    }
    
    return false;
  }
  
  public Rectangle getPlayerRectangel() {
    return playerRectangel;
  }
}
