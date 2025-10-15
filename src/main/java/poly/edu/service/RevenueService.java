package poly.edu.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import poly.edu.dto.OrderDTO;
import poly.edu.dto.RevenueDTO;
import poly.edu.dto.VIPCustomerDTO;
import poly.edu.entity.Order;
import poly.edu.repository.OrderRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RevenueService {
    private final OrderRepository orderRepository;

    public RevenueDTO calculateRevenue(LocalDateTime startDate, LocalDateTime endDate) {
        List<Order> orders;
        
        if (startDate != null && endDate != null) {
            orders = orderRepository.findCompletedOrdersBetweenDates(startDate, endDate);
        } else {
            orders = orderRepository.findCompletedOrders();
        }
        
        RevenueDTO dto = new RevenueDTO();
        dto.setTotalOrders(orders.size());
        dto.setTotalRevenue(orders.stream()
                .map(Order::getTotalAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add));
        
        return dto;
    }

    public List<OrderDTO> getCompletedOrders(LocalDateTime startDate, LocalDateTime endDate) {
        List<Order> orders;
        
        if (startDate != null && endDate != null) {
            orders = orderRepository.findCompletedOrdersBetweenDates(startDate, endDate);
        } else {
            orders = orderRepository.findCompletedOrders();
        }
        
        return orders.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<VIPCustomerDTO> getVIPCustomers(BigDecimal threshold) {
        List<Order> completedOrders = orderRepository.findCompletedOrders();
        
        Map<String, BigDecimal> customerSpending = new HashMap<>();
        
        for (Order order : completedOrders) {
            String customerName = order.getAccount() != null ? 
                    order.getAccount().getFullName() : "Khách lẻ";
            BigDecimal amount = order.getTotalAmount() != null ? 
                    order.getTotalAmount() : BigDecimal.ZERO;
            
            customerSpending.merge(customerName, amount, BigDecimal::add);
        }
        
        return customerSpending.entrySet().stream()
                .filter(entry -> entry.getValue().compareTo(threshold) >= 0)
                .map(entry -> {
                    VIPCustomerDTO dto = new VIPCustomerDTO();
                    dto.setCustomerName(entry.getKey());
                    dto.setTotalSpent(entry.getValue());
                    return dto;
                })
                .sorted((a, b) -> b.getTotalSpent().compareTo(a.getTotalSpent()))
                .collect(Collectors.toList());
    }

    private OrderDTO convertToDTO(Order order) {
        OrderDTO dto = new OrderDTO();
        dto.setId(order.getId());
        dto.setOrderDate(order.getOrderDate());
        dto.setStatus(order.getStatus());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setReceiverName(order.getReceiverName());
        if (order.getAccount() != null) {
            dto.setCustomerName(order.getAccount().getFullName());
        }
        return dto;
    }
}
