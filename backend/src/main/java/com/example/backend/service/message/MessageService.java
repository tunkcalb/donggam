package com.example.backend.service.message;

import com.example.backend.dto.message.GetStatusListDto;
import com.example.backend.dto.message.GetMessageDetailDto;
import com.example.backend.dto.message.GetMessageListDto;
import com.example.backend.dto.message.LikeMessageDto;
import com.example.backend.dto.message.MessageDto;
import com.example.backend.dto.message.SendMessageDto.Request;
import com.example.backend.dto.message.SendMessageDto.Response;
import com.example.backend.entity.mariaDB.status.Status;
import com.example.backend.entity.mariaDB.member.Member;
import com.example.backend.entity.mariaDB.message.Message;
import com.example.backend.exception.ErrorCode;
import com.example.backend.exception.type.CustomException;
import com.example.backend.repository.mariaDB.member.MemberRepository;
import com.example.backend.repository.mariaDB.status.CustomStatusRepository;
import com.example.backend.repository.mariaDB.status.StatusRepository;
import com.example.backend.repository.mariaDB.message.CustomMessageRepository;
import com.example.backend.repository.mariaDB.message.MessageRepository;
import com.example.backend.util.ImageUtil;
import com.example.backend.util.fcm.FCMNotificationRequestDto;
import com.example.backend.util.fcm.FCMNotificationService;
import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional
public class MessageService {
    private final MessageRepository messageRepository;
    private final MemberRepository memberRepository;
    private final CustomMessageRepository customMessageRepository;
    private final StatusRepository statusRepository;
    private final CustomStatusRepository customStatusRepository;
    private final ImageUtil imageUtil;
    private final FCMNotificationService fcmNotificationService;

    public Response sendMessage(Long memberId, MultipartFile img, Request request)
        throws IOException {

        if(img == null && request.getContent() == null) {
            throw new CustomException("내용이 없습니다.", ErrorCode.INVALID_INPUT_VALUE);
        }
        Long statusId = request.getStatusId();
        Status status = customStatusRepository.findWIthMemberById(statusId);

        Member from = memberRepository.findById(memberId).get();
        if(from == null) {
            throw new CustomException("존재하지 않는 유저입니다", ErrorCode.NOT_SAME_DATA_VALUE);
        }

        String imageAddress = null;
        if(img != null){
            imageAddress = imageUtil.uploadImage(img, "message");
        }

        Message message = messageRepository.save(request.toMessageEnitty(from, imageAddress, status));

        // fcm 알림 보내기
        FCMNotificationRequestDto messageAlert = FCMNotificationRequestDto.builder()
                .memberId(status.getMember().getId())
                .title("동감")
                .body("새 쪽지를 확인해보세요")
                .build();

        fcmNotificationService.sendNotificationByToken(messageAlert);

        return message.toSendMessageResponse();
    }

    public GetStatusListDto.Response getMessageAndStatusList(Long memberId) {

        List<Status> statusList = statusRepository.findAllByMemberId(memberId);

        if(statusList.size() == 0) throw new NoSuchElementException("상태 메세지가 없습니다");

        return GetStatusListDto.Response.builder()
                .statusList(statusList.stream().map(Status::toStatusDto).collect(Collectors.toList()))
                .build();
    }

    public GetMessageListDto.Response getMessageList(Long statusId) {
        List<Message> list = customMessageRepository.findAllByStatusId(statusId);
        List<MessageDto> messageList = list.stream().map(Message::toMessageDto).collect(Collectors.toList());
        return GetMessageListDto.Response.builder()
                .messageList(messageList)
                .build();
    }

    public GetMessageDetailDto.Response getMessageDetail(Long messageId) {
        Message message = messageRepository.findById(messageId).get();
        if(message == null) {
            throw new CustomException("메세지가 존재하지 않습니다.", ErrorCode.NOT_SAME_DATA_VALUE);
        }
        return message.toMessageDetailDto();
    }

    public void readMessage(Long messageId) {
        Message message= messageRepository.findById(messageId).get();
        message.setRead(true);
        messageRepository.save(message);
    }

    public void likeMessage(LikeMessageDto.Request request) {
        Message message = messageRepository.findById(request.getMessageId()).get();
        message.setLiked(request.getIsLiked());
        messageRepository.save(message);
    }
}
