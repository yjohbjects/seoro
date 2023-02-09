package com.seoro.seoro.service.Library;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.seoro.seoro.domain.dto.Book.BookReportDto;
import com.seoro.seoro.domain.dto.Book.OwnBookDto;
import com.seoro.seoro.domain.dto.Book.OwnCommentDto;
import com.seoro.seoro.domain.dto.Book.ReadBookDto;
import com.seoro.seoro.domain.dto.Book.ReviewDto;
import com.seoro.seoro.domain.dto.Group.GroupShowDto;
import com.seoro.seoro.domain.dto.Library.LibraryDto;
import com.seoro.seoro.domain.dto.Member.MemberDto;
import com.seoro.seoro.domain.dto.ResultResponseDto;
import com.seoro.seoro.domain.entity.Book.BookReport;
import com.seoro.seoro.domain.entity.Book.OwnBook;
import com.seoro.seoro.domain.entity.Book.ReadBook;
import com.seoro.seoro.domain.entity.Book.Review;
import com.seoro.seoro.domain.entity.Groups.GroupJoin;
import com.seoro.seoro.domain.entity.Groups.Groups;
import com.seoro.seoro.domain.entity.Member.Member;
import com.seoro.seoro.repository.Book.BookReportRepository;
import com.seoro.seoro.repository.Book.OwnBookRepository;
import com.seoro.seoro.repository.Book.ReadBookRepository;
import com.seoro.seoro.repository.Member.MemberRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class LibraryServiceImpl implements LibraryService {
	private final BookReportRepository bookReportRepository;
	private final ReadBookRepository readBookRepository;
	private final OwnBookRepository ownBookRepository;
	private final MemberRepository memberRepository;

	@Override
	public LibraryDto libraryMain(Long memberId) {
		LibraryDto responseDto = new LibraryDto();
		Member member = memberRepository.findById(memberId).orElseThrow(() -> new NoSuchElementException("회원이 없습니다."));

		// 본인 여부
		if(memberId.equals(member.getMemberId())) {
			responseDto.setOwn(true);
		} else {
			responseDto.setOwn(false);
		}

		// 프로필
		responseDto.setMemberInfo(new MemberDto(member));

		// 참여 모임
		List<GroupShowDto> groupShowDto = new ArrayList<>();
		List<GroupJoin> findGroupJoin = member.getGroupJoins();
		for(GroupJoin groupJoin : findGroupJoin) {
			Groups groups = groupJoin.getGroups();
			groupShowDto.add(GroupShowDto.builder()
				.groupProfile(groups.getGroupProfile())
				.groupDescrib(groups.getGroupIntroduction())
				.groupName(groups.getGroupName())
				.build());
		}
		responseDto.setMyGroups(groupShowDto);

		// 보유 도서
		List<OwnBook> ownBooks = member.getOwnBooks();
		List<OwnBookDto> ownBookDtoList = new ArrayList<>();
		for(OwnBook ownBook : ownBooks) {
			ownBookDtoList.add(new OwnBookDto(ownBook));
		}
		responseDto.setMyOwnBooks(ownBookDtoList);

		// 읽은 도서
		List<ReadBook> readBooks = member.getReadBooks();
		List<ReadBookDto> readBookDtoList = new ArrayList<>();
		for(ReadBook readBook : readBooks) {
			readBookDtoList.add(new ReadBookDto(readBook));
		}
		responseDto.setMyReadBooks(readBookDtoList);

		// 한줄평 카운트
		Long countOwnComment = ownBookRepository.countByMember(member);
		responseDto.setMyOwnComment(countOwnComment);

		// 리뷰 카운트
		Long countReview = readBookRepository.countByMember(member);
		responseDto.setMyReview(countReview);

		// 빌린 도서
		// 채팅방 api 완성 후 추가

		// 친구
		
		return responseDto;
	}

	// 도서 상세 조회

	@Override
	public List<GroupShowDto> viewMyGroup(Long memberId) {
		// 코드 중복 리팩토링 필요
		Member member = memberRepository.findById(memberId).orElseThrow(() -> new NoSuchElementException("회원이 없습니다."));

		List<GroupShowDto> groupShowDto = new ArrayList<>();
		List<GroupJoin> findGroupJoin = member.getGroupJoins();
		for(GroupJoin groupJoin : findGroupJoin) {
			Groups groups = groupJoin.getGroups();
			groupShowDto.add(GroupShowDto.builder()
				.groupProfile(groups.getGroupProfile())
				.groupDescrib(groups.getGroupIntroduction())
				.groupName(groups.getGroupName())
				.build());
		}

		return groupShowDto;
	}

	// 보유 도서 등록

	// 보유 도서 삭제


	@Override
	public List<OwnCommentDto> viewMyComment(Long memberId) {
		Member member = memberRepository.findById(memberId).orElseThrow(() -> new NoSuchElementException("회원이 없습니다."));

		List<OwnBook> ownBooks = member.getOwnBooks();
		List<OwnCommentDto> commentDtoList = new ArrayList<>();
		for(OwnBook ownBook : ownBooks) {
			commentDtoList.add(new OwnCommentDto(ownBook));
		}

		return commentDtoList;
	}

	// 읽은 도서 삭제

	@Override
	public List<ReviewDto> viewMyReview(Long memberId) {
		Member member = memberRepository.findById(memberId).orElseThrow(() -> new NoSuchElementException("회원이 없습니다."));

		List<Review> reviews = member.getReviews();
		List<ReviewDto> reviewDtoList = new ArrayList<>();
		for(Review review : reviews) {
			reviewDtoList.add(new ReviewDto(review));
		}

		return reviewDtoList;
	}

	@Override
	public List<BookReportDto> viewBookReportList(Long memberId) {
		List<BookReportDto> bookReportList = bookReportRepository.findBookReportsByMemberId(memberId);
		return bookReportList;
	}

	@Override
	public ResultResponseDto makeBookReport(BookReportDto requestDto, Long memberId) {
		ResultResponseDto resultResponseDto = new ResultResponseDto();

		BookReport bookReport = new BookReport();
		ReadBook readBook = new ReadBook();
		readBook = readBookRepository.findByReadBookId(requestDto.getReadBookId()).get();
		Member nowMember = memberRepository.findById(memberId).get();

		// 이미지 저장 추가

		bookReport = BookReport.builder()
			.readBook(readBook)
			.member(nowMember)
			.bookReportTitle(requestDto.getBookReportTitle())
			.bookReportContent(requestDto.getBookReportContent())
			.build();

		bookReportRepository.save(bookReport);
		resultResponseDto.setResult(true);

		return resultResponseDto;
	}

	@Override
	public BookReportDto viewBookReport(Long bookReportId) {
		Optional<BookReport> bookReport = bookReportRepository.findById(bookReportId);
		BookReport responseBookReport = bookReport.get();

		BookReportDto responsetDto = new BookReportDto
			(responseBookReport.getReadBook().getReadBookId(), responseBookReport.getBookReportTitle(), responseBookReport.getBookReportContent());

		return responsetDto;
	}

	@Override
	public ResultResponseDto modifyBookReport(BookReportDto requestDto, Long bookReportId) {
		ResultResponseDto resultResponseDto = new ResultResponseDto();

		Optional<BookReport> bookReport = bookReportRepository.findById(bookReportId);
		if(bookReport.isPresent()) {
			BookReport newBookReport = bookReport.get();

			// 이미지 수정 추가

			newBookReport = BookReport.builder()
				.bookReportId(bookReportId)
				.readBook(newBookReport.getReadBook())
				.bookReportTitle(requestDto.getBookReportTitle())
				.bookReportContent(requestDto.getBookReportContent())
				.build();

			bookReportRepository.save(newBookReport);
			resultResponseDto.setResult(true);
		}

		return resultResponseDto;
	}

	@Override
	public ResultResponseDto removeBookReport(Long bookReportId) {
		ResultResponseDto resultResponseDto = new ResultResponseDto();

		Optional<BookReport> bookReport = bookReportRepository.findById(bookReportId);
		if(bookReport.isPresent()) {
			bookReportRepository.delete(bookReport.get());
			resultResponseDto.setResult(true);
		}

		return resultResponseDto;
	}

	// 친구 추가
	// 친구 삭제
	// 친구 목록 조회
}
