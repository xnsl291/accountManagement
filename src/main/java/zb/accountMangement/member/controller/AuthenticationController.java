package zb.accountMangement.member.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import zb.accountMangement.member.dto.SignUpDto;
import zb.accountMangement.member.service.AuthenticationService;

@RestController
@RequiredArgsConstructor
public class AuthenticationController {
  private final AuthenticationService authenticationService;

  @PostMapping("/sign-up")
  public ResponseEntity<String> signUp(SignUpDto signUpDto){
      authenticationService.signUp(signUpDto);
      return ResponseEntity.ok().body("User registered successfully");

  }

//  User user = validationService.getUserFromToken(token);
//      if(validationService.verifyUserNStudy(token,studyId)) {
//    checkListService.createChecklist(user.getId(), studyId, createChecklistDto.getTitle(), AccessType.STUDY);
//    return ResponseEntity.ok().body("체크리스트 생성 완료");
//  }
//      return ResponseEntity.badRequest().body("사용자 아이디와 토큰정보가 일치하지 않습니다 ");
//}

}
