package hello.itemservice.web.validation;

import hello.itemservice.domain.item.Item;
import hello.itemservice.domain.item.ItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@Slf4j
@RequestMapping("/validation/v2/items")
@RequiredArgsConstructor
public class ValidationItemControllerV2 {

    private final ItemRepository itemRepository;
    private final ItemValidater itemValidater;

/**
 * @InitBinder
 * 컨트롤러가 호출 될때마다 적용되는 사항.
 * 해당 컨트롤러에서만 적용됨. 다른 컨트롤러는 적용 안됨.
 * -> 검증기 적용.
 */
    @InitBinder
    public void init(WebDataBinder dataBinder) {
        dataBinder.addValidators(itemValidater);
    }
    @GetMapping
    public String items(Model model) {
        List<Item> items = itemRepository.findAll();
        model.addAttribute("items", items);
        return "validation/v2/items";
    }

    @GetMapping("/{itemId}")
    public String item(@PathVariable long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "validation/v2/item";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("item", new Item());
        return "validation/v2/addForm";
    }

//    @PostMapping("/add")
    public String addItemV1(
            @ModelAttribute Item item,
            RedirectAttributes redirectAttributes,
            BindingResult bindingResult, // 검증 오류 결과를 보관
            Model model
    ) {

        //검증 로직
        if(!StringUtils.hasText(item.getItemName())){
            bindingResult.addError(new FieldError("item", "itemName", "상품 이름은 필수입니다."));
        }
        if(item.getPrice() == null || item.getPrice() < 1000 || item.getPrice() > 1000000){
            bindingResult.addError(new FieldError("item", "price", "가격은 1,000~1000000 까지 허용합니다."));
        }
        if (item.getQuantity() == null || item.getQuantity() >= 9999) {
            bindingResult.addError(new FieldError("item", "quantity", "수량은 최대 9,999까지 허용합니다."));
        }
        if (item.getPrice() != null && item.getQuantity() != null) {
            int resultPrice = item.getPrice()*item.getQuantity();
            if (resultPrice < 10000) {
                bindingResult.addError(new ObjectError("item", "가격 x 수량의 합은 10,000이상이어야 합니다. 현재값 =  "+resultPrice));
            }
        }

        //검증 실패하면 다시 입력 폼으로
        if(bindingResult.hasErrors()){
            log.info("errors= {}", bindingResult);
            return "validation/v2/addForm";
        }


        // 성공 로직
        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v2/items/{itemId}";
    }
//    @PostMapping("/add")
    public String addItemV2(
            @ModelAttribute Item item,
            RedirectAttributes redirectAttributes,
            BindingResult bindingResult, // 검증 오류 결과를 보관
            Model model
    ) {

        //검증 로직
        if(!StringUtils.hasText(item.getItemName())){
            bindingResult.addError(
                    new FieldError(
                            "item",
                            "itemName",
                            item.getItemName(),
                            false,
                            null,
                            null,
                            "상품 이름은 필수입니다."
                    )
            );
        }
        if(item.getPrice() == null || item.getPrice() < 1000 || item.getPrice() > 1000000){
            bindingResult.addError(
                    new FieldError(
                            "item",
                            "price",
                            item.getPrice(),
                            false,
                            null,
                            null,
                            "가격은 1,000~1000000 까지 허용합니다."
                    )
            );
        }
        if (item.getQuantity() == null || item.getQuantity() >= 9999) {
            bindingResult.addError(
                    new FieldError(
                            "item",
                            "quantity",
                            item.getQuantity(),
                            false,
                            null,
                            null,
                            "수량은 최대 9,999까지 허용합니다."
                    )
            );
        }
        if (item.getPrice() != null && item.getQuantity() != null) {
            int resultPrice = item.getPrice()*item.getQuantity();
            if (resultPrice < 10000) {
                bindingResult.addError(
                        new ObjectError(
                                "item",
                                null,
                                null,
                                "가격 x 수량의 합은 10,000이상이어야 합니다. 현재값 =  "+resultPrice
                        )
                );
            }
        }

        //검증 실패하면 다시 입력 폼으로
        if(bindingResult.hasErrors()){
            log.info("errors= {}", bindingResult);
            return "validation/v2/addForm";
        }


        // 성공 로직
        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v2/items/{itemId}";
    }

//    @PostMapping("/add")
    public String addItemV3(
            @ModelAttribute Item item,
            RedirectAttributes redirectAttributes,
            BindingResult bindingResult, // 검증 오류 결과를 보관
            Model model
    ) {

        log.info("objectName={}", bindingResult.getObjectName());
        log.info("target={}", bindingResult.getTarget());

        //검증 로직
        if(!StringUtils.hasText(item.getItemName())){
            bindingResult.addError(
                    new FieldError(
                            "item",
                            "itemName",
                            item.getItemName(),
                            false,
                            new String[]{"required.item.itemName"},
                            null,
                            null
                    )
            );
        }
        if(item.getPrice() == null || item.getPrice() < 1000 || item.getPrice() > 1000000){
            bindingResult.addError(
                    new FieldError(
                            "item",
                            "price",
                            item.getPrice(),
                            false,
                            new String[]{"range.item.price"},
                            new Object[]{1000, 1000000},
                            null
                    )
            );
        }
        if (item.getQuantity() == null || item.getQuantity() >= 9999) {
            bindingResult.addError(
                    new FieldError(
                            "item",
                            "quantity",
                            item.getQuantity(),
                            false,
                            new String[]{"max.item.quantity"},
                            new Object[]{9999},
                            null
                    )
            );
        }
        if (item.getPrice() != null && item.getQuantity() != null) {
            int resultPrice = item.getPrice()*item.getQuantity();
            if (resultPrice < 10000) {
                bindingResult.addError(
                        new ObjectError(
                                "item",
                                new String[]{"totalPriceMin"},
                                new Object[]{10000, resultPrice},
                                null
                        )
                );
            }
        }

        //검증 실패하면 다시 입력 폼으로
        if(bindingResult.hasErrors()){
            log.info("errors= {}", bindingResult);
            return "validation/v2/addForm";
        }


        // 성공 로직
        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v2/items/{itemId}";
    }

//    @PostMapping("/add")
    public String addItemV4(
            @ModelAttribute Item item,
            RedirectAttributes redirectAttributes,
            BindingResult bindingResult, // 검증 오류 결과를 보관
            Model model
    ) {
//      bindingResult 는 타겟을 이미 알고있음. => rejectValue , reject 사용 / reject : 거절
        log.info("objectName={}", bindingResult.getObjectName());
        log.info("target={}", bindingResult.getTarget());

        //검증 로직
        if(!StringUtils.hasText(item.getItemName())){
            bindingResult.rejectValue("itemName","required");
        }
        if(item.getPrice() == null || item.getPrice() < 1000 || item.getPrice() > 1000000){
            bindingResult.rejectValue("price","range", new Object[]{1000, 1000000}, null);
        }
        if (item.getQuantity() == null || item.getQuantity() >= 9999) {
            bindingResult.rejectValue("quantity","max", new Object[]{9999}, null);
        }
        if (item.getPrice() != null && item.getQuantity() != null) {
            int resultPrice = item.getPrice()*item.getQuantity();
            if (resultPrice < 10000) {
                bindingResult.reject("totalPriceMin", new Object[]{10000, resultPrice}, null);
            }
        }

        //검증 실패하면 다시 입력 폼으로
        if(bindingResult.hasErrors()){
            log.info("errors= {}", bindingResult);
            return "validation/v2/addForm";
        }


        // 성공 로직
        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v2/items/{itemId}";
    }

//    @PostMapping("/add")
    public String addItemV5(
            @ModelAttribute Item item,
            RedirectAttributes redirectAttributes,
            BindingResult bindingResult, // 검증 오류 결과를 보관
            Model model
    ) {
//      bindingResult 는 타겟을 이미 알고있음. => rejectValue , reject 사용 / reject : 거절
        log.info("objectName={}", bindingResult.getObjectName());
        log.info("target={}", bindingResult.getTarget());

        //검증 로직
        ValidationUtils.rejectIfEmptyOrWhitespace(bindingResult,"itemName","required");
/*      위와 같은 뜻
        if(!StringUtils.hasText(item.getItemName())){
            bindingResult.rejectValue("itemName","required");
        }
*/


        if(item.getPrice() == null || item.getPrice() < 1000 || item.getPrice() > 1000000){
            bindingResult.rejectValue("price","range", new Object[]{1000, 1000000}, null);
        }
        if (item.getQuantity() == null || item.getQuantity() >= 9999) {
            bindingResult.rejectValue("quantity","max", new Object[]{9999}, null);
        }
        if (item.getPrice() != null && item.getQuantity() != null) {
            int resultPrice = item.getPrice()*item.getQuantity();
            if (resultPrice < 10000) {
                bindingResult.reject("totalPriceMin", new Object[]{10000, resultPrice}, null);
            }
        }

        //검증 실패하면 다시 입력 폼으로
        if(bindingResult.hasErrors()){
            log.info("errors= {}", bindingResult);
            return "validation/v2/addForm";
        }


        // 성공 로직
        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v2/items/{itemId}";

        // rejectValue() 호출 ->
        // MessageCodesResolver를 사용해서 검증 오류 메시지 코드 생성 ->
        // new FieldError()를 생성하면서 메시지 코드들을 보관 ->
        // th:errors에서 메시지 코드들로 메시지를 순서대로 메시지에서 찾고 노출

    }

//    @PostMapping("/add")
    public String addItemV6(
            @ModelAttribute Item item,
            RedirectAttributes redirectAttributes,
            BindingResult bindingResult, // 검증 오류 결과를 보관
            Model model
    ) {
        //error가 있으면 itemValidater에서 bindingResult에 error를 담는다.
        // 없으면 errors = null 이 될...껄?
        itemValidater.validate(item, bindingResult);

        //검증 실패하면 다시 입력 폼으로
        if(bindingResult.hasErrors()){
            log.info("errors= {}", bindingResult);
            return "validation/v2/addForm";
        }


        // 성공 로직
        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v2/items/{itemId}";

    }

    /**
     * @Validated : Item에 대해서 자동으로 검증기가 실행됨. -> bindingResult에 담겨진다는 뜻 / 검증기를 실행하라 는 어노테이션
     *  validator를 직접 호출하는 부분이 사라지고 검증대상 앞에 @Validated 가 붙음.
     *  검증기가 여러개일경우 supports(Class<?> clazz) 가 동작하게 되서 class를 확인해서 true인 것만 동작.
     */
    @PostMapping("/add")
    public String addItemV7(
            @Validated @ModelAttribute Item item,
            RedirectAttributes redirectAttributes,
            BindingResult bindingResult, // 검증 오류 결과를 보관
            Model model
    ) {
        //error가 있으면 itemValidater에서 bindingResult에 error를 담는다.
        // 없으면 errors = null 이 될...껄?
        itemValidater.validate(item, bindingResult);

        //검증 실패하면 다시 입력 폼으로
        if(bindingResult.hasErrors()){
            log.info("errors= {}", bindingResult);
            return "validation/v2/addForm";
        }


        // 성공 로직
        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v2/items/{itemId}";

        // rejectValue() 호출 ->
        // MessageCodesResolver를 사용해서 검증 오류 메시지 코드 생성 ->
        // new FieldError()를 생성하면서 메시지 코드들을 보관 ->
        // th:errors에서 메시지 코드들로 메시지를 순서대로 메시지에서 찾고 노출

    }

    @GetMapping("/{itemId}/edit")
    public String editForm(@PathVariable Long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "validation/v2/editForm";
    }

    @PostMapping("/{itemId}/edit")
    public String edit(@PathVariable Long itemId, @ModelAttribute Item item) {
        itemRepository.update(itemId, item);
        return "redirect:/validation/v2/items/{itemId}";
    }

}

