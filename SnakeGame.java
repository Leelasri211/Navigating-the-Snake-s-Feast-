import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Random;

public class SnakeGame {
    private static final int GAME_WIDTH = 600;
    private static final int GAME_HEIGHT = 600;
    private static final int SNAKE_SIZE = 20;
    private static final int FOOD_SIZE = 20;
    private static final int SPEED = 100;

    private Snake snake;
    private Food food;
    private Timer timer;
    private JFrame frame;
    private JPanel panel;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new SnakeGame();
            }
        });
    }

    public SnakeGame() {
        snake = new Snake();
        food = new Food();
        timer = new Timer(SPEED, new GameListener());
        frame = new JFrame("Snake Game");
        panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                draw(g);
            }
        };
        panel.setPreferredSize(new Dimension(GAME_WIDTH, GAME_HEIGHT));
        frame.add(panel);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        timer.start();

        panel.getInputMap().put(KeyStroke.getKeyStroke("UP"), "moveUp");
        panel.getActionMap().put("moveUp", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                snake.setDirection(Direction.UP);
            }
        });

        panel.getInputMap().put(KeyStroke.getKeyStroke("DOWN"), "moveDown");
        panel.getActionMap().put("moveDown", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                snake.setDirection(Direction.DOWN);
            }
        });

        panel.getInputMap().put(KeyStroke.getKeyStroke("LEFT"), "moveLeft");
        panel.getActionMap().put("moveLeft", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                snake.setDirection(Direction.LEFT);
            }
        });

        panel.getInputMap().put(KeyStroke.getKeyStroke("RIGHT"), "moveRight");
        panel.getActionMap().put("moveRight", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                snake.setDirection(Direction.RIGHT);
            }
        });
    }
    

    private void draw(Graphics g) {
        // Draw grey borders
        g.setColor(Color.GRAY);
        g.fillRect(0, 0, GAME_WIDTH, SNAKE_SIZE); // Top border
        g.fillRect(0, GAME_HEIGHT - SNAKE_SIZE, GAME_WIDTH, SNAKE_SIZE); // Bottom border
        g.fillRect(0, SNAKE_SIZE, SNAKE_SIZE, GAME_HEIGHT - 2 * SNAKE_SIZE); // Left border
        g.fillRect(GAME_WIDTH - SNAKE_SIZE, SNAKE_SIZE, SNAKE_SIZE, GAME_HEIGHT - 2 * SNAKE_SIZE); // Right border
    
        // Fill the game area with white color
        g.setColor(Color.WHITE);
        g.fillRect(SNAKE_SIZE, SNAKE_SIZE, GAME_WIDTH - 2 * SNAKE_SIZE, GAME_HEIGHT - 2 * SNAKE_SIZE);
    
        // Draw the snake in black color
        g.setColor(Color.BLACK);
        for (Segment segment : snake.getBody()) {
            g.fillRect(segment.getX(), segment.getY(), SNAKE_SIZE, SNAKE_SIZE);
        }
    
        // Draw the food in red color
        g.setColor(Color.RED);
        g.fillRect(food.getX(), food.getY(), FOOD_SIZE, FOOD_SIZE);
    }
    
    
    private class GameListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            snake.move();
            if (snake.eat(food)) {
                food.respawn();
            }
            panel.repaint();
        }
    }

    private class Snake {
        private ArrayList<Segment> body;
        private Direction direction;

        public Snake() {
            body = new ArrayList<>();
            body.add(new Segment(GAME_WIDTH / 2, GAME_HEIGHT / 2));
            direction = Direction.RIGHT;
        }

        public ArrayList<Segment> getBody() {
            return body;
        }

        public void move() {
            Segment newHead = new Segment(body.get(0).getX(), body.get(0).getY());
            switch (direction) {
                case UP:
                    newHead.setY(newHead.getY() - SNAKE_SIZE);
                    break;
                case DOWN:
                    newHead.setY(newHead.getY() + SNAKE_SIZE);
                    break;
                case LEFT:
                    newHead.setX(newHead.getX() - SNAKE_SIZE);
                    break;
                case RIGHT:
                    newHead.setX(newHead.getX() + SNAKE_SIZE);
                    break;
            }
        
            // Check for collision with itself
            for (int i = 1; i < body.size(); i++) {
                if (newHead.getX() == body.get(i).getX() && newHead.getY() == body.get(i).getY()) {
                    gameOver();
                    return;
                }
            }
        
            // Check for collision with perimeter
            if (newHead.getX() < SNAKE_SIZE || newHead.getX() >= GAME_WIDTH - SNAKE_SIZE ||
                newHead.getY() < SNAKE_SIZE || newHead.getY() >= GAME_HEIGHT - SNAKE_SIZE) {
                gameOver();
                return;
            }
        
            // Wrap around the borders
            if (newHead.getX() < 0) {
                newHead.setX(GAME_WIDTH - SNAKE_SIZE);
            } else if (newHead.getX() >= GAME_WIDTH) {
                newHead.setX(0);
            }
            if (newHead.getY() < 0) {
                newHead.setY(GAME_HEIGHT - SNAKE_SIZE);
            } else if (newHead.getY() >= GAME_HEIGHT) {
                newHead.setY(0);
            }
        
            body.add(0, newHead);
            body.remove(body.size() - 1);
        }
        
        
        
        private void gameOver() {
            timer.stop();
            JOptionPane.showMessageDialog(frame, "Game Over", "Game Over", JOptionPane.INFORMATION_MESSAGE);
        }
        
        

        public boolean eat(Food food) {
            if (body.get(0).getX() == food.getX() && body.get(0).getY() == food.getY()) {
                body.add(0, new Segment(food.getX(), food.getY()));
                return true;
            }
            return false;
        }
       
        public void setDirection(Direction direction) {
            this.direction = direction;
        }
    }

    private class Segment {
        private int x;
        private int y;
    
        public Segment(int x, int y) {
            this.x = x;
            this.y = y;
        }
    
        public int getX() {
            return x;
        }
    
        public void setX(int x) {
            this.x = x;
        }
    
        public int getY() {
            return y;
        }
    
        public void setY(int y) {
            this.y = y;
        }
    }
    

    private class Food {
        private int x;
        private int y;
        private Random random;

        public Food() {
            random = new Random();
            respawn();
        }

        public void respawn() {
            // Respawn the food in a random place
            int maxX = (GAME_WIDTH - 2 * SNAKE_SIZE) / FOOD_SIZE; // Adjusted for border thickness
            int maxY = (GAME_HEIGHT - 2 * SNAKE_SIZE) / FOOD_SIZE; // Adjusted for border thickness
            x = (random.nextInt(maxX) + 1) * FOOD_SIZE + SNAKE_SIZE; // Adjusted for border thickness
            y = (random.nextInt(maxY) + 1) * FOOD_SIZE + SNAKE_SIZE; // Adjusted for border thickness
        
            // Check if the new location is not inside the snake's body or on the border
            for (Segment segment : snake.getBody()) {
                if (x == segment.getX() && y == segment.getY()) {
                    respawn();
                    return;
                }
            }
        
            // Check if the new location is on the border
            if (x == 0 || x == GAME_WIDTH - FOOD_SIZE || y == 0 || y == GAME_HEIGHT - FOOD_SIZE) {
                respawn();
                return;
            }
        }
        
        

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }
    }

    private enum Direction {
        UP, DOWN, LEFT, RIGHT
    }
}
