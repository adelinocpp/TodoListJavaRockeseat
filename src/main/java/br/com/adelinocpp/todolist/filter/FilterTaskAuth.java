package br.com.adelinocpp.todolist.filter;

import java.io.IOException;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import at.favre.lib.crypto.bcrypt.BCrypt;
import br.com.adelinocpp.todolist.user.IUserRepository;
//import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
//import jakarta.servlet.ServletRequest;
//import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


/* @Component
public class FilterTaskAuth implements Filter{

    @Autowired
    private IUserRepository userRepository;
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        System.out.println("Chegou no filtro");
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        String authorization = httpRequest.getHeader("Authorization");
        if (authorization != null){
            var authEncoded = authorization.substring("Basic".length()).trim();
            byte[] authDecoded = Base64.getDecoder().decode(authEncoded);
            var authString = new String(authDecoded);
            String[] credentials = authString.split(":");          
            var userName = credentials[0];
            var passWord = credentials[1];
            System.out.println("Authorization");
            System.out.println(userName);
            System.out.println(passWord);

            
            var user = this.userRepository.findByUsername(userName);
            if (user == null){
                httpResponse.sendError(401, "Usuário sem autorização.");
            }
            else{

                chain.doFilter(httpRequest,httpResponse);
            }
        }
        chain.doFilter(httpRequest,httpResponse);
        //throw new UnsupportedOperationException("Unimplemented method 'doFilter'");
    }
    
} */


 
@Component
public class FilterTaskAuth extends OncePerRequestFilter{

    
    @Autowired
    private IUserRepository userRepository;
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
     throws ServletException, IOException {

        var servletPath = request.getServletPath();
        if (servletPath.startsWith("/tasks/"))
        {
            String authorization = request.getHeader("Authorization");
            if (authorization != null){
                var authEncoded = authorization.substring("Basic".length()).trim();
                byte[] authDecoded = Base64.getDecoder().decode(authEncoded);
                var authString = new String(authDecoded);
                String[] credentials = authString.split(":");          
                var userName = credentials[0];
                var passWord = credentials[1];
                var user = this.userRepository.findByUsername(userName);
                if (user == null){
                    response.sendError(401, "Usuário sem autorização.");
               
                }
                else{
                    var passwordVerify = BCrypt.verifyer().verify(passWord.toCharArray(),user.getPassword());
                    if (passwordVerify.verified){
                        request.setAttribute("idUser", user.getId());
                        filterChain.doFilter(request,response);
                    }
                    else{
                        response.sendError(401, "Usuário sem autorização.");
                    }
                }
            }
        }else{
            filterChain.doFilter(request,response);
        }
        //throw new UnsupportedOperationException("Unimplemented method 'doFilterInternal'");
    }
    
}
