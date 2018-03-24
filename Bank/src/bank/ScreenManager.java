/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bank;

/**
 *
 * @author wolly1477
 */
public class ScreenManager 
{
    
    Screen currentScreen;
    Screen previousScreen;
    public ScreenManager()
    {
       currentScreen = new StartScreen();
    }
    public void setScreen(Screen screen)
    {
        currentScreen.setVisible(false);
        previousScreen  = currentScreen;
        currentScreen = screen;
        showScreen();
    }
    public void goBack()
    {
        Screen temp = currentScreen;
        currentScreen = previousScreen;
        previousScreen = temp;
        currentScreen.setVisible(true);
        previousScreen.setVisible(false);
    }
    
    public void ExitSession(){
        setScreen(new StartScreen());
    }

    void showScreen() {
        currentScreen.setVisible(true);
    }

    void setSerialData(String data) {
        currentScreen.passData(data);
            
    }
}
