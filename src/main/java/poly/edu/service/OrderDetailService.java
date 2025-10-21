package poly.edu.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import poly.edu.entity.OrderDetail;
import poly.edu.repository.OrderDetailRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderDetailService {
    private final OrderDetailRepository orderDetailRepository;

    public List<OrderDetail> getDetailsByOrderId(Integer orderId) {
        return orderDetailRepository.findByOrderId(orderId);
    }
}
