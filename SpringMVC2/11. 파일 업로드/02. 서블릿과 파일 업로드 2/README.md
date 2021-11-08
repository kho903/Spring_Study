# 서블릿과 파일 업로드 2
- 서블릿이 제공하는 `Part`
- 먼저 파일을 업로드를 하려면 실제 파일이 저장되는 경로가 필요하다.
- 해당 경로에 실제 폴더를 만들어 두고, 다음에 만들어진 경로를 입력해둔다.
## application.properties
```text
file.dir=파일 업로드 경로 설정(예): /Users/kimjihun/dev/file/
```
### 주의
- 해당 경로에 실제 폴더를 미리 만들어 두기
- application.properties 에서 설정할 때 마지막에 / (슬래시)가 포함된다.

## ServletUploarControllerV2
```java
package hello.upload.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collection;

@Slf4j
@Controller
@RequestMapping("/servlet/v2")
public class ServletUploadControllerV2 {
    @Value("${file.dir}")
    private String fileDir;

    @GetMapping("/upload")
    public String newFile() {
        return "upload-form";
    }

    @PostMapping("/upload")
    public String saveFileV1(HttpServletRequest request) throws
            ServletException, IOException {
        log.info("request={}", request);
        String itemName = request.getParameter("itemName");
        log.info("itemName={}", itemName);
        Collection<Part> parts = request.getParts();
        log.info("parts={}", parts);
        for (Part part : parts) {
            log.info("==== PART ====");
            log.info("name={}", part.getName());
            Collection<String> headerNames = part.getHeaderNames();
            for (String headerName : headerNames) {
                log.info("header {}: {}", headerName,
                        part.getHeader(headerName));
            }
            //편의 메서드
            //content-disposition; filename
            log.info("submittedFileName={}", part.getSubmittedFileName());
            log.info("size={}", part.getSize()); //part body size
            //데이터 읽기
            InputStream inputStream = part.getInputStream();
            String body = StreamUtils.copyToString(inputStream,
                    StandardCharsets.UTF_8);
            log.info("body={}", body);
            //파일에 저장하기
            if (StringUtils.hasText(part.getSubmittedFileName())) {
                String fullPath = fileDir + part.getSubmittedFileName();
                log.info("파일 저장 fullPath={}", fullPath);
                part.write(fullPath);
            }
        }
        return "upload-form";
    }
}
```
- `application.properties`에서 설정한 `file.dir`의 값을 주입한다.
- 멀티파트 형식은 전송 데이터를 하나하나 각각 부분(`part`)으로 나누어 전송한다. `parts`에는 이렇게 나누어진
데이터가 각각 담긴다.
- 서블릿이 제공하는 `Part`는 멀티파트 형식을 편리하게 읽을 수 있는 다양한 메서드를 제공한다.

## Part 주요 메서드
- `part.getSubmittedFileName()` : 클라이언트가 전달한 파일명
- `part.getInputStream()` : Part의 전송 데이터를 읽을 수 있다.
- `part.write(...)` : Part를 통해 전송된 데이터를 저장할 수 있다.

### 전송 및 실행 실행
- `itemName` : 상품A
- `file` : 스크린샷.png

`http://localhost:8080/servlet/v2/upload`
- 결과 로그
```text
==== PART ====
name=itemName
header content-disposition: form-data; name="itemName"
submittedFileName=null
size=7
body=상품A
==== PART ====
name=file
header content-disposition: form-data; name="file"; filename="스크린샷.png"
header content-type: image/png
submittedFileName=스크린샷.png
size=112384
body=qwlkjek2ljlese...
파일 저장 fullPath=/Users/kimyounghan/study/file/스크린샷.png
```
- 파일 저장 경로에 가보면 실제 파일이 저장된 것을 확인할 수 있다. 만약 저장이 되지 않았다면 파일 저장 경로를 다시 확인

### 참고
- 큰 용량의 파일을 업로드를 테스트 할 때는 로그가 너무 많이 남아서 다음 옵션을 끄는 것이 좋다.
- `logging.level.org.apache.coyote.http11=debug`
- 다음 부분도 파일의 바이너리 데이터를 모두 출력하므로 끄는 것이 좋다.
- `log.info("body={}", body);`

### 정리
- 서블릿이 제공하는 `Part`는 편하기는 하지만, `HttpServletRequest`를 사용해야 하고, 추가로 파일 부분만
구현하려면 여러가지 코드를 넣어야 한다.