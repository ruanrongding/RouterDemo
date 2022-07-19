package com.example.commonlibs.service;

public class ServiceFactory {
    private LoginService loginService;

    /**
     * 禁止外部创建 ServiceFactory 对象
     */
    private ServiceFactory() {
    }

    /**
     * 通过静态内部类方式实现 ServiceFactory 的单例
     */
    public static ServiceFactory getInstance() {
        return Inner.serviceFactory;
    }

    private static class Inner {
        private static ServiceFactory serviceFactory = new ServiceFactory();
    }

    /**
     * 接收 Login 组件实现的 Service 实例
     */
    public void setLoginService(LoginService loginService) {
        this.loginService = loginService;
    }

    /**
     * 返回 Login 组件的 Service 实例
     */
    public LoginService getLoginService() {
        if (loginService == null) {
            return new EmptyService();
        } else {
            return loginService;
        }
    }
}

