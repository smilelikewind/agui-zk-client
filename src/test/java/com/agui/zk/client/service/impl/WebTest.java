package com.agui.zk.client.service.impl;

import com.agui.zk.client.AbstractTest;
import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.lingshou.entity.Response;
import com.lingshou.lion.client.Lion;
import com.lingshou.openrack.basic.bo.ShelfAddressBO;
import com.lingshou.openrack.basic.bo.qrcode.OpenRackBean;
import com.lingshou.openrack.basic.request.qrcode.QRCodeDTO;
import com.lingshou.openrack.basic.request.qrcode.QRCodeResponseBean;
import com.lingshou.openrack.basic.response.QueryAddressShelfsByMachineIdResp;
import com.lingshou.openrack.basic.service.common.AddressShelfQueryService;
import com.lingshou.openrack.basic.service.qrcode.QRCodeFacade;
import com.lingshou.order.service.OrderService;
import com.lingshou.order.service.bo.OrderBO;
import com.lingshou.pay.cashier.common.enums.PaymentChannel;
import com.lingshou.web.base.model.GrouponListVO;
import com.member.member.order.pay.req.BuyMemCardReq;
import com.member.member.order.pay.resp.PayOrderResponse;
import com.member.member.order.pay.service.MemberPayService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WebTest extends AbstractTest {

    private static final String mm = "kylin-web.lottery_tab_config";
    private static final String APP_H5_HEAD = "app-rackportal-web.h5_head";
    private static final String APP_STOREVALUE = "app-rackportal-web.storevalue";

    private static final String APP_RECOMMEND_TITLE = "groupon-base-service.App.recommendTitle";
    private static final String APP_RECOMMEND_LINK = "groupon-base-service.App.recommendLink";
    private static final String APP_RECOMMEND_ICON = "groupon-base-service.App.recommendIcon";


    @Autowired
    private MemberPayService memberPayService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private QRCodeFacade qrCodeFacade;

    @Autowired
    AddressShelfQueryService addressShelfQueryService;


    @Test
    public void testQuery(){
        BuyMemCardReq buyMemCardReq = new BuyMemCardReq();
        buyMemCardReq.setCardId(8);
        buyMemCardReq.setPayChannel(PaymentChannel.WEIXIN);
        buyMemCardReq.setUserAgent("weixin");
        buyMemCardReq.setUserIp("127.0.0.1");
        buyMemCardReq.setUserId(52);
        buyMemCardReq.setUserPhone("18363993921");
        PayOrderResponse payOrderResponse = memberPayService.buyRechargeCardForM(buyMemCardReq);
        System.out.println(JSON.toJSON(payOrderResponse));
    }


    @Test
    public void testucQuery(){
            List<OrderBO> orderBOS = orderService.ucQuery(893,1,1);
            if(orderBOS != null && orderBOS.size() >0){
                int rackId = orderBOS.get(0).getSellerId();
                System.out.println("rackId: " + rackId);
                Map<String, String> map = new HashMap<>();
                map.put("rackId", String.valueOf(rackId));
                //通过rackId拿uuid
                QRCodeDTO qrCodeDTO = qrCodeFacade.queryByQrcode(3,map);
                if(qrCodeDTO == null){
                    System.out.println("qrCodeDTO is null");
                    return;
                }
                String uuid = qrCodeDTO.getUuid();
                System.out.println("uuid: " + uuid);
            }else {
                System.out.println("用户无购买行为");
            }
    }

    @Test
    public void selectByMachineIds(){
        List list = Lists.newArrayList();
        list.add(70);
        QueryAddressShelfsByMachineIdResp queryAddressShelfsByMachineIdResp =  addressShelfQueryService.selectByMachineIds(list);
        List<ShelfAddressBO>  list2 = queryAddressShelfsByMachineIdResp.getShelfAddressBos();
        for(ShelfAddressBO item : list2){
            System.out.println("item.getLatitude():" + item.getLatitude());
            System.out.println("item.getLongitude():" + item.getLongitude());
        }
    }

    @Test
    public void testLion(){
        System.out.println("groupon-base-service.App.recommendTitle: " + Lion.get(APP_RECOMMEND_TITLE) );
        System.out.println("groupon-base-service.App.recommendLink :" + Lion.get(APP_RECOMMEND_LINK) );
        System.out.println("groupon-base-service.App.recommendIcon :" + Lion.get(APP_RECOMMEND_ICON) );
        System.out.println("APP_STOREVALUE ：" + Lion.get(APP_STOREVALUE));


    }

    @Test
    public void testGroup(){
        String uuid = "8dfcd380-4539-4e57-8793-244fc8dcacdf";
        System.out.println("-----------------------");
        Response<QRCodeResponseBean<OpenRackBean>> queryBeanResponse = qrCodeFacade.queryByUuid(uuid, OpenRackBean.class);
        if (!Response.isSuccess(queryBeanResponse) || queryBeanResponse.getResult() == null || queryBeanResponse.getResult().getBean() == null) {
            System.out.println("queryBeanResponse 有问题");
        }
        int rackId = queryBeanResponse.getResult().getBean().getRackId();
    }


    @Test
    public void test(){
//        GrouponRecommendBO grouponRecommendBO = new GrouponRecommendBO();
//        grouponRecommendBO.setTitle("shujie");
//        grouponRecommendBO.setDealPrice(666);
//
////        GrouponListVO grouponListVO = new GrouponListVO();
//        GrouponRecommendVO grouponRecommendVO = new GrouponRecommendVO();
//        BeanUtils.copyProperties(grouponRecommendBO,grouponRecommendVO);
//        System.out.println(grouponRecommendVO.getTitle());


        GrouponListVO grouponListVO = new GrouponListVO();




    }




}
