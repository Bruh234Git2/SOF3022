package poly.edu.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import poly.edu.dto.AccountDTO;
import poly.edu.entity.Account;
import poly.edu.entity.Role;
import poly.edu.repository.AccountRepository;
import poly.edu.repository.RoleRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminAccountService {
    private final AccountRepository accountRepository;
    private final RoleRepository roleRepository;

    public List<AccountDTO> getAllAccounts() {
        return accountRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<AccountDTO> searchAccounts(String keyword, String roleName) {
        List<Account> accounts = accountRepository.findAll();
        
        return accounts.stream()
                .filter(acc -> {
                    boolean matchKeyword = keyword == null || keyword.trim().isEmpty() ||
                            acc.getEmail().toLowerCase().contains(keyword.toLowerCase()) ||
                            acc.getFullName().toLowerCase().contains(keyword.toLowerCase());
                    
                    boolean matchRole = roleName == null || roleName.trim().isEmpty() ||
                            (acc.getRole() != null && acc.getRole().getName().equalsIgnoreCase(roleName));
                    
                    return matchKeyword && matchRole;
                })
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public AccountDTO getAccountById(Integer id) {
        return accountRepository.findById(id)
                .map(this::convertToDTO)
                .orElse(null);
    }

    @Transactional
    public AccountDTO updateAccount(Integer id, AccountDTO dto) {
        Account account = accountRepository.findById(id).orElse(null);
        if (account == null) return null;
        
        account.setFullName(dto.getFullName());
        account.setEmail(dto.getEmail());
        account.setPhone(dto.getPhone());
        account.setAddress(dto.getAddress());
        
        if (dto.getRoleName() != null) {
            Role role = roleRepository.findByName(dto.getRoleName().toUpperCase()).orElse(null);
            if (role != null) {
                account.setRole(role);
            }
        }
        
        return convertToDTO(accountRepository.save(account));
    }

    @Transactional
    public boolean deleteAccount(Integer id) {
        if (!accountRepository.existsById(id)) return false;
        accountRepository.deleteById(id);
        return true;
    }

    private AccountDTO convertToDTO(Account account) {
        AccountDTO dto = new AccountDTO();
        dto.setId(account.getId());
        dto.setUsername(account.getUsername());
        dto.setFullName(account.getFullName());
        dto.setEmail(account.getEmail());
        dto.setPhone(account.getPhone());
        dto.setAddress(account.getAddress());
        dto.setStatus(account.getStatus());
        if (account.getRole() != null) {
            dto.setRoleName(account.getRole().getName());
        }
        return dto;
    }
}
