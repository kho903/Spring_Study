# 예제로 구현하는 파일 업로드, 다운로드
- 실제 파일이나 이미지를 업로드, 다운로드 할 때는 몇가지 고려할 사항이 있다.

## 요구사항
- 상품을 관리
    - 상품 이름
    - 첨부파일 하나
    - 이미지 파일 여러개
- 첨부파일을 업로드 / 다운로드 할 수 있다.
- 업로드한 이미지를 웹 브라우저에서 확인할 수 있다.

## Item - 상품 도메인
```java
package hello.upload.domain;

import lombok.Data;

import java.util.List;

@Data
public class Item {
    private Long id;
    private String itemName;
    private UploadFile attachFile;
    private List<UploadFile> imageFiles;
}
```
## ItemRepository - 상품 리포지토리
```java
package hello.upload.domain;

import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
public class ItemRepository {
    private final Map<Long, Item> store = new HashMap<>();
    private long sequence = 0L;

    public Item save(Item item) {
        item.setId(++sequence);
        store.put(item.getId(), item);
        return item;
    }

    public Item findById(Long id) {
        return store.get(id);
    }
}
```
## UploadFile - 업로드 파일 정보 보관
```java
package hello.upload.domain;

import lombok.Data;

@Data
public class UploadFile {
    private String uploadFileName;
    private String storeFileName;

    public UploadFile(String uploadFileName, String storeFileName) {
        this.uploadFileName = uploadFileName;
        this.storeFileName = storeFileName;
    }
}
```
- `uploadFileName` : 고객이 업로드한 파일명
- `storeFileName` : 서버 내부에서 관리하는 파일명

- 고객이 업로드한 파일명으로 서버 내부에 파일을 저장하면 안된다.
- 왜냐하면 서로 다른 고객이 같은 파일이름을 업로드하는 경우 기존 파일 이름과 충돌 날 수 있다.
- 서버에서는 저장할 파일명이 겹치지 않도록 내부에서 관리하는 별도의 파일명이 필요하다.

## FileStore - 파일 저장과 관련된 업무 처리
```java
package hello.upload.file;

import hello.upload.domain.UploadFile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class FileStore {
    @Value("${file.dir}")
    private String fileDir;

    public String getFullPath(String filename) {
        return fileDir + filename;
    }

    public List<UploadFile> storeFiles(List<MultipartFile> multipartFiles)
            throws IOException {
        List<UploadFile> storeFileResult = new ArrayList<>();
        for (MultipartFile multipartFile : imageFiles) {
            if (!imageFile.isEmpty()) {
                storeFileResult.add(storeFile(multipartFile));
            }
        }
        return storeFileResult;
    }

    public UploadFile storeFile(MultipartFile multipartFile) throws IOException {
        if (multipartFile.isEmpty()) {
            return null;
        }
        String originalFilename = multipartFile.getOriginalFilename();
        String storeFileName = createStoreFileName(originalFilename);
        multipartFile.transferTo(new File(getFullPath(storeFileName)));
        return new UploadFile(originalFilename, storeFileName);
    }

    private String createStoreFileName(String originalFilename) {
        String ext = extractExt(originalFilename);
        String uuid = UUID.randomUUID().toString();
        return uuid + "." + ext;
    }

    private String extractExt(String originalFilename) {
        int pos = originalFilename.lastIndexOf(".");
        return originalFilename.substring(pos + 1);
    }
}
```
멀티파트 파일을 서버에 저장하는 역할을 담당한다.
- `createStoreFileName()` : 서버 내부에서 관리하는 파일명은 유일한 이름을 생성하는 `UUID`를 사용해서 충돌하지 않도록 한다.
- `extractExt()` : 확장자를 별도로 추출해서 서버 내부에서 관리하는 파일명에도 붙여준다.
    - 예를 들어서 고객이 `a.png`라는 이름으로 업로드를 하면 `7b95490f-5e63-4c7a-9914-a4acc4c0245c.png`와
    같이 저장한다.

## ItemForm
```java
package hello.upload.controller;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class ItemForm {
    private Long itemId;
    private String itemName;
    private List<MultipartFile> imageFiles;
    private MultipartFile attachFile;
}
```
상품 저장용 폼이다.
- `List<MultipartFile> imageFiles` : 이미지를 다중 업로드 하기 위해 `MultipartFile`을 사용했다.
- `MultipartFile attachFile` : 멀티파트는 `@ModelAttribute`에서 사용할 수 있다.

## ItemController
```java
package hello.upload.controller;

import hello.upload.domain.UploadFile;
import hello.upload.domain.Item;
import hello.upload.domain.ItemRepository;
import hello.upload.file.FileStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriUtils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ItemController {
    private final ItemRepository itemRepository;
    private final FileStore fileStore;

    @GetMapping("/items/new")
    public String newItem(@ModelAttribute ItemForm form) {
        return "item-form";
    }

    @PostMapping("/items/new")
    public String saveItem(@ModelAttribute ItemForm form, RedirectAttributes
            redirectAttributes) throws IOException {
        UploadFile attachFile = fileStore.storeFile(form.getAttachFile());
        List<UploadFile> storeImageFiles =
                fileStore.storeFiles(form.getImageFiles());
        //데이터베이스에 저장
        Item item = new Item();
        item.setItemName(form.getItemName());
        item.setAttachFile(attachFile);
        item.setImageFiles(storeImageFiles);
        itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", item.getId());
        return "redirect:/items/{itemId}";
    }

    @GetMapping("/items/{id}")
    public String items(@PathVariable Long id, Model model) {
        Item item = itemRepository.findById(id);
        model.addAttribute("item", item);
        return "item-view";
    }

    @ResponseBody
    @GetMapping("/images/{filename}")
    public Resource downloadImage(@PathVariable String filename) throws
            MalformedURLException {
        return new UrlResource("file:" + fileStore.getFullPath(filename));
    }

    @GetMapping("/attach/{itemId}")
    public ResponseEntity<Resource> downloadAttach(@PathVariable Long itemId)
            throws MalformedURLException {
        Item item = itemRepository.findById(itemId);
        String storeFileName = item.getAttachFile().getStoreFileName();
        String uploadFileName = item.getAttachFile().getUploadFileName();
        UrlResource resource = new UrlResource("file:" +
                fileStore.getFullPath(storeFileName));
        log.info("uploadFileName={}", uploadFileName);
        String encodedUploadFileName = UriUtils.encode(uploadFileName,
                StandardCharsets.UTF_8);
        String contentDisposition = "attachment; filename=\"" +
                encodedUploadFileName + "\"";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
                .body(resource);
    }
}
```
- `@GetMapping("/items/new")` : 등록 폼을 보여준다.
- `@PostMapping("/items/new")` : 폼의 데이터를 저장하고 보여주는 화면으로 리다이렉트한다.
- `@GetMapping("/items/{id})` : 상품을 보여준다.
- `@GetMapping("/images/{filename})` : `<img>` 태그로 이미지를 조회할 때 사용한다.
`UrlResource`로 이미지 파일을 읽어서 `@ResponseBody`로 이미지 바이너리를 반환한다.
- `@GetMapping("/attach/{itemId})` : 파일을 다운로드할 때 실행한다.
  파일 다운로드시에는 고객이 업로드한 파일 이름으로 다운로드하는게 좋다. 
  이때는 `Content-Disposition` 헤더에 `attachment; filename="업로드 파일명"` 값을 주면 된다.

## 등록 폼 뷰
```html
<!DOCTYPE HTML>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="utf-8">
</head>
<body>
<div class="container">
    <div class="py-5 text-center">
        <h2>상품 등록</h2>
    </div>
    <form th:action method="post" enctype="multipart/form-data">
        <ul>
            <li>상품명 <input type="text" name="itemName"></li>
            <li>첨부파일<input type="file" name="attachFile"></li>
            <li>이미지 파일들<input type="file" multiple="multiple"
                              name="imageFiles"></li>
        </ul>
        <input type="submit"/>
    </form>
</div> <!-- /container -->
</body>
</html>
```
- 다중 파일 업로드를 하려면 `multiple="multiple"`옵션을 주면 된다.
- `ItemForm`의 다음 코드에서 여러 이미지를 받을 수 있다.
- `private List<MultipartFile> imageFiles;`

## 조회 뷰
- item-view.html
```html
<!DOCTYPE HTML>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="utf-8">
</head>
<body>
<div class="container">
    <div class="py-5 text-center">
        <h2>상품 조회</h2>
    </div>
    상품명: <span th:text="${item.itemName}">상품명</span><br/>
    첨부파일: <a th:if="${item.attachFile}" th:href="|/attach/${item.id}|"
             th:text="${item.getAttachFile().getUploadFileName()}"/><br/>
    <img th:each="imageFile : ${item.imageFiles}" th:src="|/images/$
{imageFile.getStoreFileName()}|" width="300" height="300"/>
</div> <!-- /container -->
</body>
</html>
```
- 첨부파일은 링클 걸어두고 이미지는 `<img>` 태그를 반복해서 출력한다.

### 실행 
- http://localhost:8080/items/new
- 실행해보면 하나의 첨부파일을 다운로드 업로드 하고, 여러 이미지 파일을 한 번에 업로드 할 수 있다.
