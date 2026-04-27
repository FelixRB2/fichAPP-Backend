package com.example.proyectoFichaje.config;

import com.example.proyectoFichaje.service.UsuarioService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    @Lazy
    private UsuarioService usuarioService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        final String authorizationHeader = request.getHeader("Authorization");

        String username = null;
        String jwt = null;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            try {
                username = jwtUtil.extractUsername(jwt);
                System.out.println(">>> JWT username extraído: " + username); // ← añade esto
            } catch (Exception e) {
                System.out.println(">>> Error extrayendo JWT: " + e.getMessage()); // ← y esto
            }
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            UserDetails userDetails = this.usuarioService.loadUserByUsername(username);

            System.out.println(">>> Username del token: " + username);
            System.out.println(">>> Username del userDetails: " + userDetails.getUsername());
            System.out.println(">>> Token expirado: " + jwtUtil.isTokenExpired(jwt)); // haz este método public
            System.out.println(">>> Authorities: " + userDetails.getAuthorities());
            System.out.println(">>> isEnabled: " + userDetails.isEnabled());
            System.out.println(">>> isAccountNonLocked: " + userDetails.isAccountNonLocked());
            System.out.println(">>> validateToken: " + jwtUtil.validateToken(jwt, userDetails));


            if (jwtUtil.validateToken(jwt, userDetails)) {

                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                usernamePasswordAuthenticationToken
                        .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            }
        }
        chain.doFilter(request, response);
    }
}
