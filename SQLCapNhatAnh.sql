USE ShopOMG;
GO

PRINT '========================================';
PRINT '1) CAP NHAT ANH CHO CATEGORIES';
PRINT '========================================';

-- Cập nhật ảnh cho từng Category theo ID (1..10)
UPDATE c
SET image = CASE c.id
    WHEN 1 THEN N'https://cf.shopee.vn/file/322a6d327458d60bb1b78eb2dfb63a59' -- Áo thun
    WHEN 2 THEN N'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSFvCjSnjkNsQBaaCL7v6c18w9mA4yGyBom2Q&s' -- Áo sơ mi
    WHEN 3 THEN N'https://thitruongsi.com/kinh-nghiem/wp-content/uploads/2017/06/phai-chang-quan-jeans-2-trong-1-sap-tro-thanh-xu-huong-moi-05-490x735.jpg' -- Quần jean
    WHEN 4 THEN N'https://yeepvn.sgp1.digitaloceanspaces.com/2023/03/sg-11134201-22100-aiww7x44z0iv8e.jpg' -- Quần tây
    WHEN 5 THEN N'https://cdn.shopify.com/s/files/1/0267/2164/8819/files/Sneaker_Portrait_f8eef94f-b99f-4e8f-ac4f-d4ded1d1e5df.png?v=1728631662' -- Giày thể thao
    WHEN 6 THEN N'https://product.hstatic.net/1000355922/product/giay-da-nam-buoc-day-ngoai-co-4382048__4__e362e00156044450a2c910f48858d1ba_master.jpg' -- Giày da
    WHEN 7 THEN N'https://mia.vn/media/uploads/tin-tuc/tui-deo-cheo-anime-5-1682078046.jpg' -- Túi xách
    WHEN 8 THEN N'https://down-vn.img.susercontent.com/file/sg-11134201-22100-7yqc4onb3bjve1@resize_w900_nl.webp' -- Phụ kiện
    WHEN 9 THEN N'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTDtXduzra495Wfg__SSf75KAdOXjD9Q75AdA&s' -- Áo khoác
    WHEN 10 THEN N'https://zofal.vn/wp-content/uploads/2020/12/ZD310086SK-1.jpg' -- Váy đầm
    ELSE image
END
FROM Categories c;

PRINT '✅ Da cap nhat anh cho Categories';
PRINT '';

PRINT '========================================';
PRINT '2) CAP NHAT ANH CHINH CHO PRODUCTS';
PRINT '========================================';

-- 2.1. Ảnh riêng cho Áo thun Nam/Nữ
DECLARE @TshirtMaleImage   NVARCHAR(500) = N'https://n7media.coolmate.me/uploads/September2025/ao-the-thao-nam-promax-recycle-basics1-copy-navy-logo-moi-1.jpg?aio=w-1100';
DECLARE @TshirtFemaleImage NVARCHAR(500) = N'https://image.hm.com/assets/hm/7c/61/7c61fbed34a24cb2aa9bb509f0debcccc7f04b8b.jpg?imwidth=2160';

-- Tất cả Áo thun Nam (category_id = 1, gender = 'Nam')
UPDATE Products
SET image = @TshirtMaleImage
WHERE category_id = 1
  AND gender = N'Nam';

-- Tất cả Áo thun Nữ (category_id = 1, gender = N'Nữ')
UPDATE Products
SET image = @TshirtFemaleImage
WHERE category_id = 1
  AND gender = N'Nữ';

PRINT '✅ Da cap nhat anh cho Ao thun Nam/Nu';
PRINT '';

-- 2.2. Ảnh cho các sản phẩm còn lại, quay vòng 15 URL còn lại
DECLARE @ProductImage TABLE(
    id INT IDENTITY(1,1) PRIMARY KEY,
    image_url NVARCHAR(500)
);

INSERT INTO @ProductImage(image_url) VALUES
(N'https://yame.vn/cdn/shop/files/0024805_thumb_1.jpg?v=1760801737&width=823'),
(N'https://down-vn.img.susercontent.com/file/vn-11134258-820l4-mh98b5cg7aby86'),
(N'https://image.hm.com/assets/hm/39/2f/392f7a0b5290fd3e7d93e5a53deb121cc9b9324d.jpg?imwidth=2160'),
(N'https://yame.vn/cdn/shop/files/0024729_Thumb_1.jpg?v=1760794250&width=823'),
(N'https://image.hm.com/assets/hm/43/9d/439da0151d5d77bf29181b9226aba3d587794f61.jpg?imwidth=2160'),
(N'https://image.hm.com/assets/hm/ee/29/ee296c54dbdbecbeb40ed68e5d466ebe793d3a03.jpg?imwidth=2160'),
(N'https://product.hstatic.net/200000940675/product/12_78371a7d50f6421cab1febf6bdf97cda_1024x1024.jpg'),
(N'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRxEIedJsldsrhVcZ5hYeG5vsKkFzdhI114qA&s'),
(N'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQj2ReMTuuX_NXus7wvuTMTg3_Wn0lnInZPMw&s'),
(N'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQ7i4Ye5S8b5AKxM_5kiHsbRobpryhbmhfdxQ&s'),
(N'https://bizweb.dktcdn.net/thumb/1024x1024/100/558/463/products/4d83f026-3b01-4c38-8a3d-a3fb841f21fd-2e97e3e7d8994a07954a587f29ab0d98.jpg?v=1747680809780'),
(N'https://cavatcaocap.com/wp-content/uploads/2024/12/ca-vat-xanh-navy-3-resize.webp'),
(N'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTjaE3sR2gPvXJPNAyPHAcH1wOMP49wd46sCg&s'),
(N'https://nd-res2.tichluy.vn/Upload/Deals/1024/102434_ao-khoac-nu-mong-vai-thang-mau-xanh-navy-ao-hoodie-cardigan-chat-vai-ni-mem-min-thoai-mai-form-basic-vua-van_638950301071267263.jpg'),
(N'https://images2.thanhnien.vn/528068263637045248/2023/7/16/screenshot20230716005755facebook-1689522012358957241173.jpg');

-- Gán ảnh cho các sản phẩm KHÁC áo thun (category_id <> 1), quay vòng theo ID
UPDATE p
SET p.image = pi.image_url
FROM Products p
JOIN @ProductImage pi
  ON pi.id = ((p.id - 1) % 15) + 1
WHERE p.category_id <> 1;

PRINT '✅ Da cap nhat anh chinh cho tat ca Products';
PRINT '';

PRINT '========================================';
PRINT '3) LAM LAI 3 ANH PHU CHO MOI SAN PHAM';
PRINT '========================================';

-- Tập tất cả URL (2 anh tshirt + 15 anh product + 10 anh category)
DECLARE @AllImageUrl TABLE(
    id INT IDENTITY(1,1) PRIMARY KEY,
    image_url NVARCHAR(500)
);

-- 2 ảnh áo thun Nam/Nữ
INSERT INTO @AllImageUrl(image_url) VALUES
(N'https://n7media.coolmate.me/uploads/September2025/ao-the-thao-nam-promax-recycle-basics1-copy-navy-logo-moi-1.jpg?aio=w-1100'),
(N'https://image.hm.com/assets/hm/7c/61/7c61fbed34a24cb2aa9bb509f0debcccc7f04b8b.jpg?imwidth=2160');

-- 15 ảnh sản phẩm còn lại
INSERT INTO @AllImageUrl(image_url)
SELECT image_url
FROM @ProductImage
ORDER BY id;

-- 10 ảnh categories
INSERT INTO @AllImageUrl(image_url) VALUES
(N'https://cf.shopee.vn/file/322a6d327458d60bb1b78eb2dfb63a59'),
(N'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSFvCjSnjkNsQBaaCL7v6c18w9mA4yGyBom2Q&s'),
(N'https://thitruongsi.com/kinh-nghiem/wp-content/uploads/2017/06/phai-chang-quan-jeans-2-trong-1-sap-tro-thanh-xu-huong-moi-05-490x735.jpg'),
(N'https://yeepvn.sgp1.digitaloceanspaces.com/2023/03/sg-11134201-22100-aiww7x44z0iv8e.jpg'),
(N'https://cdn.shopify.com/s/files/1/0267/2164/8819/files/Sneaker_Portrait_f8eef94f-b99f-4e8f-ac4f-d4ded1d1e5df.png?v=1728631662'),
(N'https://product.hstatic.net/1000355922/product/giay-da-nam-buoc-day-ngoai-co-4382048__4__e362e00156044450a2c910f48858d1ba_master.jpg'),
(N'https://mia.vn/media/uploads/tin-tuc/tui-deo-cheo-anime-5-1682078046.jpg'),
(N'https://down-vn.img.susercontent.com/file/sg-11134201-22100-7yqc4onb3bjve1@resize_w900_nl.webp'),
(N'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTDtXduzra495Wfg__SSf75KAdOXjD9Q75AdA&s'),
(N'https://zofal.vn/wp-content/uploads/2020/12/ZD310086SK-1.jpg');

-- Xoá toàn bộ ảnh phụ cũ
DELETE FROM ProductImages;

-- Thêm lại 3 ảnh phụ NGẪU NHIÊN cho mỗi sản phẩm
INSERT INTO ProductImages(product_id, image_url)
SELECT p.id, ai.image_url
FROM Products p
CROSS APPLY (
    SELECT TOP 3 image_url
    FROM @AllImageUrl
    ORDER BY NEWID()
) AS ai;

PRINT '✅ Da tao lai 3 anh phu ngau nhien cho moi san pham';
PRINT '';
PRINT '✅ HOAN TAT CAP NHAT ANH CHO DB ShopOMG';
GO

USE ShopOMG;
GO

PRINT '========================================';
PRINT 'CAP NHAT ANH CHO PRODUCTS THEO NHOM & GIOI TINH';
PRINT '========================================';

--------------------------------------------------
-- ÁO THUN (category_id = 1) - ĐÃ ĐỊNH NGHĨA TRƯỚC
--------------------------------------------------
DECLARE @TshirtMale   NVARCHAR(500) = N'https://n7media.coolmate.me/uploads/September2025/ao-the-thao-nam-promax-recycle-basics1-copy-navy-logo-moi-1.jpg?aio=w-1100';
DECLARE @TshirtFemale NVARCHAR(500) = N'https://image.hm.com/assets/hm/7c/61/7c61fbed34a24cb2aa9bb509f0debcccc7f04b8b.jpg?imwidth=2160';

UPDATE Products
SET image = @TshirtMale
WHERE category_id = 1 AND gender = N'Nam';

UPDATE Products
SET image = @TshirtFemale
WHERE category_id = 1 AND gender = N'Nữ';

--------------------------------------------------
-- ÁO SƠ MI (category_id = 2) - Nam / Nữ
--------------------------------------------------
DECLARE @ShirtMale   NVARCHAR(500) = N'https://yame.vn/cdn/shop/files/0024805_thumb_1.jpg?v=1760801737&width=823';
DECLARE @ShirtFemale NVARCHAR(500) = N'https://down-vn.img.susercontent.com/file/vn-11134258-820l4-mh98b5cg7aby86';

UPDATE Products
SET image = @ShirtMale
WHERE category_id = 2 AND gender = N'Nam';

UPDATE Products
SET image = @ShirtFemale
WHERE category_id = 2 AND gender = N'Nữ';

--------------------------------------------------
-- QUẦN JEAN (category_id = 3) - Nam / Nữ
--------------------------------------------------
DECLARE @JeanMale   NVARCHAR(500) = N'https://image.hm.com/assets/hm/39/2f/392f7a0b5290fd3e7d93e5a53deb121cc9b9324d.jpg?imwidth=2160';
DECLARE @JeanFemale NVARCHAR(500) = N'https://yame.vn/cdn/shop/files/0024729_Thumb_1.jpg?v=1760794250&width=823';

UPDATE Products
SET image = @JeanMale
WHERE category_id = 3 AND gender = N'Nam';

UPDATE Products
SET image = @JeanFemale
WHERE category_id = 3 AND gender = N'Nữ';

--------------------------------------------------
-- QUẦN TÂY (category_id = 4) - Nam / Nữ
--------------------------------------------------
DECLARE @TrousersMale   NVARCHAR(500) = N'https://image.hm.com/assets/hm/43/9d/439da0151d5d77bf29181b9226aba3d587794f61.jpg?imwidth=2160';
DECLARE @TrousersFemale NVARCHAR(500) = N'https://image.hm.com/assets/hm/ee/29/ee296c54dbdbecbeb40ed68e5d466ebe793d3a03.jpg?imwidth=2160';

UPDATE Products
SET image = @TrousersMale
WHERE category_id = 4 AND gender = N'Nam';

UPDATE Products
SET image = @TrousersFemale
WHERE category_id = 4 AND gender = N'Nữ';

--------------------------------------------------
-- GIÀY THỂ THAO (category_id = 5) - Nam / Nữ
--------------------------------------------------
DECLARE @SneakerMale   NVARCHAR(500) = N'https://product.hstatic.net/200000940675/product/12_78371a7d50f6421cab1febf6bdf97cda_1024x1024.jpg';
DECLARE @SneakerFemale NVARCHAR(500) = N'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRxEIedJsldsrhVcZ5hYeG5vsKkFzdhI114qA&s';

UPDATE Products
SET image = @SneakerMale
WHERE category_id = 5 AND gender = N'Nam';

UPDATE Products
SET image = @SneakerFemale
WHERE category_id = 5 AND gender = N'Nữ';

--------------------------------------------------
-- GIÀY DA (category_id = 6) - Nam / Nữ
--------------------------------------------------
DECLARE @LeatherMale   NVARCHAR(500) = N'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQj2ReMTuuX_NXus7wvuTMTg3_Wn0lnInZPMw&s';
DECLARE @LeatherFemale NVARCHAR(500) = N'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQ7i4Ye5S8b5AKxM_5kiHsbRobpryhbmhfdxQ&s';

UPDATE Products
SET image = @LeatherMale
WHERE category_id = 6 AND gender = N'Nam';

UPDATE Products
SET image = @LeatherFemale
WHERE category_id = 6 AND gender = N'Nữ';

--------------------------------------------------
-- TÚI XÁCH (category_id = 7) - Unisex
--------------------------------------------------
DECLARE @BagImage NVARCHAR(500) = N'https://bizweb.dktcdn.net/thumb/1024x1024/100/558/463/products/4d83f026-3b01-4c38-8a3d-a3fb841f21fd-2e97e3e7d8994a07954a587f29ab0d98.jpg?v=1747680809780';

UPDATE Products
SET image = @BagImage
WHERE category_id = 7;

--------------------------------------------------
-- PHỤ KIỆN (category_id = 8) - Unisex
--------------------------------------------------
DECLARE @AccessoryImage NVARCHAR(500) = N'https://cavatcaocap.com/wp-content/uploads/2024/12/ca-vat-xanh-navy-3-resize.webp';

UPDATE Products
SET image = @AccessoryImage
WHERE category_id = 8;

--------------------------------------------------
-- ÁO KHOÁC (category_id = 9) - Nam / Nữ
--------------------------------------------------
DECLARE @JacketMale   NVARCHAR(500) = N'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQ-OHnChYrdRmbE2D_uuAoDbajezAkYfPt7dg&s';
DECLARE @JacketFemale NVARCHAR(500) = N'https://bizweb.dktcdn.net/thumb/1024x1024/100/119/564/products/ao-khoac-nu-han-quoc-4159.jpg?v=1708316429203';

UPDATE Products
SET image = @JacketMale
WHERE category_id = 9 AND gender = N'Nam';

UPDATE Products
SET image = @JacketFemale
WHERE category_id = 9 AND gender = N'Nữ';

--------------------------------------------------
-- VÁY ĐẦM (category_id = 10) - Nữ
--------------------------------------------------
DECLARE @DressImage NVARCHAR(500) = N'https://images2.thanhnien.vn/528068263637045248/2023/7/16/screenshot20230716005755facebook-1689522012358957241173.jpg';

UPDATE Products
SET image = @DressImage
WHERE category_id = 10;
-- (Trong script DB gốc, tất cả category_id = 10 đều gender = N'Nữ')

PRINT '✅ Da map URL anh cho tat ca nhom san pham';
GO