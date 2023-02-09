package com.seoro.seoro.service.Library;

import java.io.IOException;
import java.util.List;

import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import com.seoro.seoro.domain.dto.Book.BookDto;
import com.seoro.seoro.domain.dto.Book.BookReportDto;
import com.seoro.seoro.domain.dto.Book.OwnBookDetailDto;
import com.seoro.seoro.domain.dto.Book.OwnBookDto;
import com.seoro.seoro.domain.dto.Book.OwnCommentDto;
import com.seoro.seoro.domain.dto.Book.ReviewDto;
import com.seoro.seoro.domain.dto.Group.GroupShowDto;
import com.seoro.seoro.domain.dto.Library.LibraryDto;
import com.seoro.seoro.domain.dto.ResultResponseDto;
import com.seoro.seoro.domain.entity.Book.Review;

@Service
public interface LibraryService {
	public LibraryDto libraryMain(Long memberId);
	public OwnBookDetailDto viewOwnBookDetail(String isbn) throws IOException, ParseException;
	public List<GroupShowDto> viewMyGroup(Long memberId);
	public OwnBookDto makeOwnBookWithIsbn(Long memberId, Long isbn);
	public OwnBookDto makeOwnBookWithSearch(Long memberId, Long isbn);
	public ResultResponseDto deleteOwnBook(Long memberId, String isbn);
	public ResultResponseDto deleteReadBook(Long memberId, String isbn);
	public List<OwnCommentDto> viewMyComment(Long memberId);
	public List<ReviewDto> viewMyReview(Long memberId);
	public List<BookReportDto> viewBookReportList(Long memberId);
	public ResultResponseDto makeBookReport(BookReportDto requestDto, Long memberId);
	public BookReportDto viewBookReport(Long bookReportId);
	public ResultResponseDto modifyBookReport(BookReportDto requestDto, Long bookReportId);
	public ResultResponseDto removeBookReport(Long bookReportId);
}
