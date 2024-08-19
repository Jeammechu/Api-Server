package com.jmt.board.controllers;

import com.jmt.board.entities.Board;
import com.jmt.board.entities.BoardData;
import com.jmt.board.exceptions.BoardNotFoundException;
import com.jmt.board.services.BoardConfigInfoService;
import com.jmt.board.services.BoardDeleteService;
import com.jmt.board.services.BoardInfoService;
import com.jmt.board.services.BoardSaveService;
import com.jmt.global.ListData;
import com.jmt.global.Utils;
import com.jmt.global.exceptions.BadRequestException;
import com.jmt.global.rests.JSONData;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/board")
@RequiredArgsConstructor
public class BoardController {
    private final BoardConfigInfoService configInfoService;
    private final BoardInfoService infoService;
    private final BoardSaveService saveService;
    private final BoardDeleteService deleteService;
    private final Utils utils;
    private final Validator validator;

    // 게시판 설정
    @GetMapping("/config/{bid}")
    public JSONData getConfig(@PathVariable String bid) {

        Board board = configInfoService.get(bid).orElseThrow(BoardNotFoundException::new);

        return new JSONData(board);

    }
    // 글쓰기
    @PostMapping("/write/{bid}")
    public ResponseEntity<JSONData> write(@PathVariable("bid") String bid, @RequestBody @Valid RequestBoard form, Errors errors) {
        form.setBid(bid);
        form.setMode("write");

        return save(form, errors);

    }
    // 글 수정
    @PatchMapping("/update/{seq}")
    public ResponseEntity<JSONData> update(@PathVariable("seq") Long seq, @RequestBody @Valid RequestBoard form, Errors errors) {
        form.setSeq(seq);
        form.setMode("update");

        return save(form, errors);
    }

    // 글 작성, 수정처리
    private ResponseEntity<JSONData> save(RequestBoard form , Errors errors) {
        validator.validate(form, errors);

        if (errors.hasErrors()) { // 검증 실패
            throw new BadRequestException(utils.getErrorMessages(errors));
        }

        BoardData data = saveService.save(form);

        JSONData jsonData = new JSONData(data);
        HttpStatus status = HttpStatus.CREATED;
        jsonData.setStatus(status);

        return ResponseEntity.status(status).body(jsonData);

    }
    @GetMapping("/info/{seq}")
    public JSONData info(@PathVariable("seq") Long seq) {
        BoardData item = infoService.get(seq);

        return new JSONData(item);
    }

    @GetMapping("/list/{bid}")
    public JSONData list(@PathVariable("bid") String bid ,@ModelAttribute BoardDataSearch search) {
        ListData<BoardData> data = infoService.getList(bid, search);

        return new JSONData(data);

    }

    @DeleteMapping("/delete/{seq}")
    public JSONData delete(@PathVariable("seq") Long seq) {
        BoardData item = deleteService.delete(seq);

        return new JSONData(item);
    }
}