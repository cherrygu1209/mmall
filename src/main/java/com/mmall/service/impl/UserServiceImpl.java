package com.mmall.service.impl;

import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.common.TokenCache;
import com.mmall.dao.UserMapper;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import com.mmall.util.MD5Util;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service("iUserService")
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserMapper userMapper;


    @Override
    public ServerResponse<User> login(String username, String password) {
        int resultCount = userMapper.checkUserName(username);
        if(resultCount == 0){
            return ServerResponse.createByErrorMessage("User does not exist");
        }

        //MD5加密
        String md5Password = MD5Util.MD5EncodeUtf8(password);

        User user = userMapper.selectLogin(username,md5Password);
        if(user == null){
            return ServerResponse.createByErrorMessage("Wrong password");
        }
        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccessMessage("Login Success");
    }

    public ServerResponse<String> register(User user){
        ServerResponse validResponse = this.checkValid(user.getUsername(),Const.USERNAME);
        if(validResponse.isSuccess()){
            return validResponse;
        }

        validResponse = this.checkValid(user.getEmail(),Const.EMAIL);
        if(validResponse.isSuccess()){
            return validResponse;
        }

        user.setRole(Const.Role.ROLE_CUSTOMER);
        //MD5 加密
        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));

        int resultCount = userMapper.insert(user);
        if(resultCount == 0){
            return ServerResponse.createByErrorMessage("Register Failed");
        }
        return ServerResponse.createBySuccessMessage("Register success");
    }

    public ServerResponse<String> checkValid(String str, String type){
        if(org.apache.commons.lang3.StringUtils.isNoneBlank(type)){
            //开始校验
            if(Const.USERNAME.equals(type)){
                int resultCount = userMapper.checkUserName(str);
                if(resultCount > 0){
                    return ServerResponse.createByErrorMessage("User has already existed");
                }
            }
            if(Const.EMAIL.equals(type)){
                int resultCount = userMapper.checkEmail(str);
                if(resultCount > 0){
                    return ServerResponse.createByErrorMessage("Email address has already existed");
                }
            }
        }else{
            return ServerResponse.createByErrorMessage("Argument Error");
        }
        return ServerResponse.createBySuccessMessage("Valid success");
    }

    public ServerResponse<String> selectQuestion(String username){
        ServerResponse<String> validResponse = this.checkValid(username,Const.USERNAME);
        if(validResponse.isSuccess()){
            //user does not exist
            return ServerResponse.createByErrorMessage("User does not exist");
        }
        String question = userMapper.selectQuestionByUsername(username);
        if(StringUtils.isNoneBlank(question)){
            return ServerResponse.createBySuccessMessage(question);
        }
        return ServerResponse.createByErrorMessage("Question is blank");
    }

    public ServerResponse<String> checkAnswer(String username, String question, String answer){
        int resultCount = userMapper.checkAnswer(username,question,answer);
        if(resultCount > 0){
            String forgetToken = UUID.randomUUID().toString();
            TokenCache.setKey(TokenCache.TOKEN_PREFIX + username, forgetToken);
            return ServerResponse.createBySuccess(forgetToken);
        }
        return ServerResponse.createByErrorMessage("Answer is wrong");
    }

    public ServerResponse<String> forgetResetPassword(String username, String passwordNew, String forgetToken){
        if(StringUtils.isNoneBlank(forgetToken)){
            return ServerResponse.createByErrorMessage("token need");
        }
        ServerResponse<String> validResponse = this.checkValid(username,Const.USERNAME);
        if(validResponse.isSuccess()){
            //user does not exist
            return ServerResponse.createByErrorMessage("User does not exist");
        }
        String token = TokenCache.getKey(TokenCache.TOKEN_PREFIX + username);
        if(StringUtils.isBlank(token)){
            return ServerResponse.createByErrorMessage("Token expired");
        }
        if(StringUtils.equals(forgetToken,token)){
            String md5Password = MD5Util.MD5EncodeUtf8(passwordNew);
            int rowCount = userMapper.updatePasswordByUsername(username,md5Password);
            if(rowCount > 0){
                return ServerResponse.createBySuccessMessage("update password success");
            }
        }else{
            return ServerResponse.createByErrorMessage("Token error");
        }
        return ServerResponse.createByErrorMessage("Update password fail");
    }

    public ServerResponse<String> resetPassword(String passwordOld, String passwordNew, User user){
        int resultCount = userMapper.checkPassword(MD5Util.MD5EncodeUtf8(passwordOld),user.getId());
        if(resultCount == 0){
            return ServerResponse.createByErrorMessage("Password error");
        }
        user.setPassword(MD5Util.MD5EncodeUtf8(passwordNew));
        int updateCount = userMapper.updateByPrimaryKeySelective(user);
        if(updateCount > 0){
            return ServerResponse.createBySuccessMessage("Reset password success");
        }
        return ServerResponse.createByErrorMessage("Reset password fail");
    }

    public ServerResponse<User> updateInformation(User user){
        //username cannot be updated
        //check email, the email cannot be current user's email if it is existed
        int resultCount = userMapper.checkEmailByUserId(user.getEmail(),user.getId());
        if(resultCount > 0){
            return ServerResponse.createByErrorMessage("email has already been existed");
        }
        //只用于更新
        User updateUser = new User();
        updateUser.setId(user.getId());
        updateUser.setEmail(user.getEmail());
        updateUser.setPhone(user.getPhone());
        updateUser.setQuestion(user.getQuestion());
        updateUser.setAnswer(user.getAnswer());

        int updateCount = userMapper.updateByPrimaryKeySelective(updateUser);
        if(updateCount > 0){
            return ServerResponse.createBySuccess("Update success",updateUser);
        }
        return ServerResponse.createByErrorMessage("Update fail");
    }

    public ServerResponse<User> getInformation(Integer userId){
        User user = userMapper.selectByPrimaryKey(userId);
        if(user == null){
            return ServerResponse.createByErrorMessage("Cannot find user");
        }
        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess(user);
    }

    //backend
    //校验是否为admin (用于category创建等)
    public ServerResponse checkAdminRole(User user){
        if(user != null && user.getRole().intValue() == Const.Role.ROLE_ADMIN){
            return ServerResponse.createBySuccess();
        }
        return ServerResponse.createByError();
    }
}
