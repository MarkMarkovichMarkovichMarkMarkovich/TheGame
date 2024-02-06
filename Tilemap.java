package gui;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
import building.Building;
import map.Feld;
import map.Map;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import launcher.Start;
import map.Player;
import character.Character;

/**
 *
 * @author guest-otfeii
 */
public class Tilemap extends JPanel implements MouseListener, MouseMotionListener, MouseWheelListener{
    Map m;
    Bar b;
    Point startPoint;
    int camX = 0;
    int camY = 0;
    int hoveredX = 0;
    int hoveredY = 0;
    
    Feld selectedFeld;
    Character selectedCharacter;
    Building selectedBuilding;
    
    
    /**
     * @param m
     */
    public Tilemap(int width, int height, Map m, Bar b) {
        super();
        this.m = m;
        this.b = b;
        this.setSize(new Dimension(width, height));
        this.setLocation(0, 0);
        this.setVisible(true);
        //Testfall
        m.setT(3, 3, "desert");
        
        camX = -(m.getWidth() * 64) / 4;
        camY = -(m.getHeight() * 64) / 4;

    }

    //painComponent; erst schwarzer Hintergrund
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        for (int i = 0; i < m.getWidth(); i++) {
            for (int j = 0; j < m.getHeight(); j++) {
                g.drawImage(m.getTerrainPicture(i, j), 64 * i + camX, 64 * j + camY, null);
                
            }
        }
        
        // zeichne markierungen für von maus berührte felder
        g.setColor(Color.DARK_GRAY);
        g.drawRect(64 * hoveredX + camX, 64 * hoveredY + camY, 64, 64);
        
        if (selectedFeld != null){
            g.setColor(Color.magenta);
            g.drawRect(64 * selectedFeld.getxPosition() + camX, 64 * selectedFeld.getyPosition() + camY, 64, 64);
        }
        
        //g.drawImage(Toolkit.getDefaultToolkit().getImage("src/GUI/res/ResourceBar.png"), 0, 0, null);
        
    }
    
    //Grenze für scroll
    private int clamp(int a, int min, int max){
        if (a > max) {
            return max;
        }
        
        if (a < min){
            return min;
        }
        
        return a;
        
    }
    
    private int booleanToInt(boolean foo) {
    int bar = 0;
    if (foo) {
        bar = 1;
    }
    return bar;
}
    
    private void setSelectedCharacter(){
        Player[] p = Start.getPlayers();
        selectedCharacter = null;
        
        for (int i = 0; i < p.length; i++) {
            Player player = p[i];
            
            if (player.getCharacter(hoveredX, hoveredY) != null){
                selectedCharacter = player.getCharacter(hoveredX, hoveredY);
            }
        }
        
        
    }
    
    private void setSelectedBuilding(){
        Player[] p = Start.getPlayers();
        selectedBuilding = null;
        
        for (int i = 0; i < p.length; i++) {
            Player player = p[i];
            
            if (player.getBuilding(hoveredX, hoveredY) != null){
                selectedBuilding = player.getBuilding(hoveredX, hoveredY);
            }
        }
        
        
    }
    

    @Override
    public void mouseClicked(MouseEvent e) {
        // angeklicktes Feld auswählen
        selectedFeld = m.getFeld(hoveredX, hoveredY);
        
        // angeklickte Character finden
        setSelectedCharacter();
        
        // angeklickte Gebäude finden
        setSelectedBuilding();
        
        
    }

    @Override
    public void mousePressed(MouseEvent e) {
        startPoint = SwingUtilities.convertPoint(this.getParent(), e.getPoint(), this.getParent());
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        startPoint = null;
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (startPoint == null) return;
        
        Point location = SwingUtilities.convertPoint(this, e.getPoint(), this.getParent());
        
        if (this.getRootPane().getBounds().contains(location)){
            Point newLocation = this.getLocation();
            newLocation.translate(location.x - startPoint.x, location.y - startPoint.y);

            camX += newLocation.x;
            camY += newLocation.y;
            
            // Grenze von unten rechts und oben links festgelegt
            limitCamera();
           
            
            startPoint = location;
        } else startPoint = null;
    }
    
    public void limitCamera(){
         int minX = (m.getWidth()*64 - Toolkit.getDefaultToolkit().getScreenSize().width)*-1;
            camX = clamp(camX, minX, 0);
            
            int minY = (m.getHeight()*64 - Toolkit.getDefaultToolkit().getScreenSize().height)*-1;
            camY = clamp(camY,minY - (b.getHeight() * booleanToInt(b.isVisible())), 0);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        Point clicked = SwingUtilities.convertPoint(this.getParent(), e.getPoint(), this.getParent());
        hoveredX = (clicked.x - camX) / 64;
        hoveredY = (clicked.y - camY ) / 64;
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        //tileSize -= e.getUnitsToScroll();
    }
}