package zb.accountMangement.common.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import zb.accountMangement.common.auth.JwtAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
//@AllArgsConstructor
public class SecurityConfig {
  private final JwtAuthenticationFilter jwtAuthenticationFilter;
  @Bean
  public BCryptPasswordEncoder bCryptPasswordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

    return http
        .cors()
        .and()
        .csrf().disable()
        .authorizeRequests()
          .antMatchers("/api/auth/login", "/api/auth/sign-up","/api/auth/find-pw/**" )
          .permitAll()
          .antMatchers("/api/accounts/**", "/api/transactions","/api/auth/**",
            "/api/member/**").hasRole("USER")
          .antMatchers("/admin/**").hasRole("ADMIN")
          .anyRequest().authenticated()
        .and()
        .formLogin().disable()
        .sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(
                SessionCreationPolicy.STATELESS))
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class).build();
  }
}

