/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.projectoUm.projecto.controller;

import com.projectoUm.projecto.modelo.Funcionario;
import com.projectoUm.projecto.storage.FuncionarioJpaController;
import java.math.BigDecimal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

/**
 *
 * @author ruben
 */
@Controller
@RequestMapping("/funcionario")
public class FuncionarioController {

    @Autowired
    FuncionarioJpaController db; 
    //é possivél adicionar outro jpa!

    //FUNCIONA !
    @RequestMapping(value = "/index")
    public ModelAndView index() {
        ModelAndView model = new ModelAndView();
        model.setViewName("funcionario/index");
        return model;
    }

    //FUNCIONA !
    @RequestMapping(value = "/list")
    public ModelAndView list() {
        ModelAndView model = new ModelAndView();
        model.addObject("funcionario", db.findFuncionarioEntities()); //envia para a pagina web -> lista utilizadores
        model.setViewName("funcionario/list");//invoca a pagina index na pasta /templates/utilizador
        return model;
    }

    //FUNCIONA !
    @RequestMapping(value = "create", method = RequestMethod.GET)
    public ModelAndView create() {
        ModelAndView model2 = new ModelAndView();
        model2.setViewName("funcionario/create");
        
        return model2;
    }

    //FUNCIONA !
    @RequestMapping(value = "create", method = RequestMethod.POST)
    public String create(
            //@RequestParam("id") int id,
            @RequestParam("tipo") int tipo,
            @RequestParam("nome") String nome,
            @RequestParam("morada") String morada,
            @RequestParam("password") String password) {
        

        try {
            db.create(new Funcionario(tipo, nome, morada, password));
            
        } catch (Exception e) {
            System.out.print("ERRO!");
            return "redirect:/funcionario/erro";
        }
        return "redirect:/funcionario/index";
    }

    @RequestMapping(value = "details/{id}", method = RequestMethod.GET)
    public ModelAndView details(@PathVariable("id") int id) {
        ModelAndView model = new ModelAndView();
        model.addObject("funcionario", db.findFuncionario(id));
        model.setViewName("funcionario/details");
        return model;
    }

    @RequestMapping(value = "details/{jj}", method = RequestMethod.POST)
    public String details(@PathVariable("jj") int jj,
            //@RequestParam("id") int id,
            @RequestParam("tipo") int tipo,
            @RequestParam("nome") String nome,
            @RequestParam("morada") String morada,
            @RequestParam("password") String password) {

        try {
            db.edit(new Funcionario(jj, tipo, nome, morada, password));
        } catch (Exception e) {
            return "redirect:/funcionario/details/" + jj;
        }

        return "redirect:/index";
    }

    
    //FUNCIONA
     @RequestMapping(value="update/{id}", method = RequestMethod.GET)
    public ModelAndView update(@PathVariable("id") Integer id){
        ModelAndView model = new ModelAndView();
        model.addObject("funcionario", db.findFuncionario(id));
        model.setViewName("funcionario/update");
        return model;
    }
    //FUNCIONA
    @RequestMapping(value="update/{jj}", method = RequestMethod.POST)
    public String update(@PathVariable("jj") int jj, 
                        //@RequestParam("id") int id,
                        @RequestParam("tipo") int tipo,
                        @RequestParam("nome") String nome,
                        @RequestParam("morada") String morada,
                        @RequestParam("password") String password
                        
            ){
        
        try {
            db.edit(new Funcionario(jj,tipo, nome, morada,password));
        } catch (Exception e) {
            return "redirect:/funcionario/update/"+jj;
        }

        return "redirect:/funcionario/index";
    }
    
    
    //FUNCIONA !
    @RequestMapping(value = "delete/{id}", method = RequestMethod.GET)
    public String delete(@PathVariable("id") int id) {
        try {
            db.destroy(id);
        } catch (Exception e) {
        }
        return "redirect:/funcionario/index";
    }

}
