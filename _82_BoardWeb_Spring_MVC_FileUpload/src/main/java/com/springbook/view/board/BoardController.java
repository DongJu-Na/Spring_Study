package com.springbook.view.board;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.springbook.biz.board.BoardFileVO;
import com.springbook.biz.board.BoardService;
import com.springbook.biz.board.BoardVO;
import com.springbook.biz.board.Criteria;
import com.springbook.biz.board.PageVO;
import com.springbook.biz.common.FileUtils;

@Controller
//board�� model ����� ��ü�� ������ HttpSession ������ �����ҿ��� ������ Ű ��(board)�� ����
@SessionAttributes("board")
public class BoardController {
	@Autowired
	private BoardService boardService;
	
	//@ModelAttribute : 1. Command ��ü �̸� ����
	//					2. View(JSP)���� ����� ������ ����
	@ModelAttribute("conditionMap")
	public Map<String, String> searchConditionMap() {
		Map<String, String> conditionMap = new HashMap<String, String>();
		conditionMap.put("����", "TITLE");
		conditionMap.put("����", "CONTENT");
		//���� ���� ReqeustServlet ������ �����ҿ� ����
		//conditionMap�̶�� Ű ������ �����Ͱ� ����
		return conditionMap;
	}

	
	@RequestMapping("/insertBoard.do")
	//Command ��ü : ����ڰ� ������ �����͸� ������ VO�� �ٷ� ����
	//				����� �Է� ���� �������� �ڵ尡 ������� ������ ����ȭ ����
	//              ����� �Է� input�� name �Ӽ��� VO ��������� �̸��� �������ִ� ���� �߿�
	public String insertBoard(BoardVO vo, HttpServletRequest request,
			MultipartHttpServletRequest mhsr) throws IOException {
		System.out.println("�� ��� ó��");
		
		int seq = boardService.getBoardSeq();
		System.out.println("seq===============================" + seq);
		FileUtils fileUtils = new FileUtils();
		List<BoardFileVO> fileList = fileUtils.parseFileInfo(seq, request, mhsr);
		
		if(CollectionUtils.isEmpty(fileList) == false) {
			boardService.insertBoardFileList(fileList);
			
		}
		
		boardService.insertBoard(vo);
		
		//ȭ�� �׺���̼�(�Խñ� ��� �Ϸ� �� �Խñ� ������� �̵�)
		return "redirect:getBoardList.do";
	}
	
	//ModelAttribute�� ���ǿ� board��� �̸����� ����� ��ü�� �ִ��� ã�Ƽ� Command��ü�� �����
	@RequestMapping(value="/updateBoard.do")
	public String updateBoard(@ModelAttribute("board") BoardVO vo, 
			HttpServletRequest request,
			MultipartHttpServletRequest mhsr) throws IOException {
		System.out.println("�� ���� ó��");
		System.out.println("�Ϸù�ȣ : " + vo.getSeq());
		System.out.println("���� : " + vo.getTitle());
		System.out.println("�ۼ��� �̸� : " + vo.getWriter());
		System.out.println("���� : " + vo.getContent());
		System.out.println("����� : " + vo.getRegDate());
		System.out.println("��ȸ�� : " + vo.getCnt());
		
		int seq = vo.getSeq();
		
		FileUtils fileUtils = new FileUtils();
		List<BoardFileVO> fileList = fileUtils.parseFileInfo(seq, request, mhsr);
		
		if(CollectionUtils.isEmpty(fileList) == false) {
			boardService.insertBoardFileList(fileList);
		}
		
		boardService.updateBoard(vo);
		return "redirect:getBoardList.do";
	}
	
	@RequestMapping(value="/deleteBoard.do")
	public String deleteBoard(BoardVO vo) {
		System.out.println("�� ���� ó��");
		
		boardService.deleteFileList(vo.getSeq());
		boardService.deleteBoard(vo);
		return "redirect:getBoardList.do";
	}
	
	@RequestMapping(value="/getBoard.do")
	public String getBoard(BoardVO vo, Model model, Criteria cri) {
		System.out.println("�� �� ��ȸ ó��");
		
		System.out.println(cri.getPageNum());
		System.out.println(cri.getAmount());
		//Model ��ü�� RequestServlet ������ �����ҿ� ����
		//RequestServlet ������ �����ҿ� �����ϴ� �Ͱ� �����ϰ� ����
		//request.setAttribute("board", boardDAO.getBoard(vo)) == model.addAttribute("board", boardDAO.getBoard(vo))
		model.addAttribute("board", boardService.getBoard(vo));
		model.addAttribute("criteria", cri);
		model.addAttribute("fileList", boardService.getBoardFileList(vo.getSeq()));
		return "getBoard.jsp";
	}
	
	@RequestMapping(value="/getBoardList.do")
	//@RequestParam : Command ��ü�� VO�� ���ΰ��� ���� ����� �Է������� ���� �޾Ƽ� ó��
	//				  value = ȭ�����κ��� ���޵� �Ķ���� �̸�(jsp�� input�� name�Ӽ� ��)
	//				  required = ���� ���� ����
	public String getBoardList(BoardVO vo, Model model, Criteria cri) {
		System.out.println("�� ��� �˻� ó��");
		System.out.println(vo.getSearchKeyword());
		//Null check
		//�α��� ȭ�鿡�� �α��μ��� �� getBoardList.do ȣ�� �� �� searchKeyword, searchCondition ���� null ���� 
		if(vo.getSearchCondition() == null) {
			vo.setSearchCondition("TITLE");
		}
		if(vo.getSearchKeyword() == null) {
			vo.setSearchKeyword("");
		}
		
		int total = boardService.selectBoardCount(vo);
		
		model.addAttribute("boardList", boardService.getBoardList(vo, cri));
		model.addAttribute("pageMaker", new PageVO(cri, total));
		return "getBoardList.jsp";
	}
	
	@RequestMapping(value="/deleteFile.do")
	@ResponseBody
	public void deleteFile(BoardFileVO vo) {
		boardService.deleteFile(vo);
	}
	
	@RequestMapping(value="fileDown.do")
	@ResponseBody
	public ResponseEntity<Resource> fileDown(@RequestParam("fileName") String fileName,
						HttpServletRequest request) throws Exception {
		//���ε� ���� ���
		String path = request.getSession().getServletContext().getRealPath("/") + "/upload/";
		
		//���ϰ��, ���ϸ����� ���ҽ� ��ü ����
		Resource resource = new FileSystemResource(path + fileName);
		
		//���� ��
		String resourceName = resource.getFilename();
		
		//Http����� �ɼ��� �߰��ϱ� ���ؼ� ��� ���� ����
		HttpHeaders headers = new HttpHeaders();
		
		try {
			//����� ���ϸ����� ÷������ �߰�
			headers.add("Content-Disposition", "attachment; filename=" + new String(resourceName.getBytes("UTF-8"),
						"ISO-8859-1"));
		} catch(UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return new ResponseEntity<Resource>(resource, headers, HttpStatus.OK);
	}
}