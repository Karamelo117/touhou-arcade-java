/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.finall;

import basedatos.Conexion;
import interfaces.FrMenu;


/**
 *
 * @author PC PRIDE STARFISH
 */
public class Finall {

    public static void main(String[] args) {
       Conexion con = new Conexion();
       FrMenu nmen = new FrMenu();
       nmen.setVisible(true);
       nmen.setLocationRelativeTo(null);
       
    }
}
