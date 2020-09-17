package org.authorizationserver.controller;

import java.util.Arrays;
import java.util.HashSet;
import java.util.UUID;

import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.authorizationserver.constrant.ClientType;
import org.authorizationserver.entity.Client;
import org.authorizationserver.entity.ClientDto;
import org.authorizationserver.service.ClientDetailsServiceImpl;
import org.authorizationserver.utils.Crypto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/client")
public class ClientController {
	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired private ClientDetailsServiceImpl clientRegistrationService;

	@GetMapping("/")
	public String hello(Model model){
		model.addAttribute("data",123123);
		return "client/dashboard";
	}
	@GetMapping("/register")
	public ModelAndView registerPage(ModelAndView mav) {
		mav.setViewName("client/register");
		mav.addObject("registry", new ClientDto());
		return mav;
	}
	
	@GetMapping("/dashboard")
    public ModelAndView dashboard(@ModelAttribute("clientId")String clientId
    							 ,@ModelAttribute("clientSecret")String clientSecret
    							 , ModelAndView mv) {
		log.info("========== ClientController :: dashboard start ==========");
		log.info("---------- clientId : " + clientId);
		log.info("---------- clientId : " + clientSecret);
        if(!StringUtils.isEmpty(clientId)) {
			mv.addObject("applications", clientRegistrationService.loadClientByClientId(clientId));
	        mv.addObject("client_secret", clientSecret);
        }
        mv.setViewName("client/dashboard");

		log.info("========== ClientController :: dashboard end ==========");
        return mv;
    }
	
	@Transactional
	@PostMapping("/save")
	public ModelAndView save(@Valid ClientDto clientDetails, ModelAndView mav , BindingResult bindingResult) {
		log.info("========== ClientController :: postmapping save start ==========");

		if(bindingResult.hasErrors()) {
			return new ModelAndView("client/register");
		}
		String randomId = UUID.randomUUID().toString();
		String randomSecret = UUID.randomUUID().toString();
		
		Client client = new Client();
		client.addAdditionalInformation("name", clientDetails.getName());
		client.setRegisteredRedirectUri(new HashSet<>(Arrays.asList("http://localhost:9000/callback")));
		client.setClientType(ClientType.PUBLIC);
		client.setClientId(randomId);
		client.setClientSecret(Crypto.sha256(randomSecret));
		client.setAccessTokenValiditySeconds(3600);
		client.setScope(Arrays.asList("read","write"));
		clientRegistrationService.addClientDetails(client);
		
		mav.setViewName("redirect:/client/dashboard");
		mav.addObject("clientId", randomId);
		mav.addObject("clientSecret", randomSecret);

		log.info("========== ClientController :: postmapping save end ==========");
		
		return mav;
	}
	
	@GetMapping("/remove")
    public ModelAndView remove(
            @RequestParam(value = "client_id", required = false) String clientId) {

        clientRegistrationService.removeClientDetails(clientId);

        ModelAndView mv = new ModelAndView("redirect:/client/dashboard");
        mv.addObject("applications",
                clientRegistrationService.listClientDetails());
        return mv;
    }
}
