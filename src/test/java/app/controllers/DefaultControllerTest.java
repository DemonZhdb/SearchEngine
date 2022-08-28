package app.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

import java.io.File;
import java.nio.file.Files;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@AutoConfigureMockMvc
@SpringBootTest()

public class DefaultControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void loginTest() throws Exception {
        this.mockMvc.perform(get("/"))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));
    }

    @Test
    public void testLoginSucces() throws Exception {
        mockMvc.perform(formLogin().user("user").password("user"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/api"))
                .andDo(print());
    }

    @Test
    public void testLoginError() throws Exception {
        mockMvc.perform(formLogin().user("user").password("1111"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?error"))
                .andDo(print());
    }

    @WithMockUser()
    @Test
    public void testLogout() throws Exception {
        mockMvc.perform(get("/logout"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?logout"))
                .andDo(print());
    }


    @WithUserDetails()
    @Test
    public void testIndexPage() throws Exception {
        File login = new ClassPathResource("templates/index.html").getFile();
        String html = new String(Files.readAllBytes(login.toPath()));
        mockMvc.perform(get("/api"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(html));
    }


}
