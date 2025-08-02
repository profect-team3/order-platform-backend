INSERT INTO p_region ("created_at", "updated_at", "region_id", "region_code", "region_name", "is_active", "full_name",
                      "sido", "sigungu", "eupmyendong")
VALUES (now(), now(), '3bf1fca4-32b4-45b7-bf99-1822aefcec7a', 'R8F8B4020', 'atque', FALSE, 'velit', 'eligendi', 'aut',
        'ducimus');
INSERT INTO p_region ("created_at", "updated_at", "region_id", "region_code", "region_name", "is_active", "full_name",
                      "sido", "sigungu", "eupmyendong")
VALUES (now(), now(), '81b7ce92-f3d3-4902-9651-1fe5bac6c26c', 'RFEE0E00A', 'qui', TRUE, 'nihil', 'minus', 'accusantium',
        'impedit');
INSERT INTO p_region ("created_at", "updated_at", "region_id", "region_code", "region_name", "is_active", "full_name",
                      "sido", "sigungu", "eupmyendong")
VALUES (now(), now(), '8adefc6f-9f7b-433b-b53c-b2fd3deb82cd', 'R03DDC7D6', 'iste', FALSE, 'deleniti', 'ipsam', 'soluta',
        'ea');
INSERT INTO p_region ("created_at", "updated_at", "region_id", "region_code", "region_name", "is_active", "full_name",
                      "sido", "sigungu", "eupmyendong")
VALUES (now(), now(), '92186ef3-6d99-471a-8849-d3e1e2528a34', 'RD517A8C8', 'commodi', TRUE, 'aliquam', 'totam',
        'reprehenderit', 'repellendus');
INSERT INTO p_region ("created_at", "updated_at", "region_id", "region_code", "region_name", "is_active", "full_name",
                      "sido", "sigungu", "eupmyendong")
VALUES (now(), now(), 'a0cd70b0-4f20-45f5-8c7d-fc1927d5d62f', 'R82DBD587', 'asperiores', FALSE, 'qui', 'dignissimos',
        'possimus', 'doloribus');
INSERT INTO p_category ("created_at", "updated_at", "category_id", "category_name")
VALUES (now(), now(), '6530a750-89b7-44af-aebc-0e008fbeccd7', 'dignissimos');
INSERT INTO p_category ("created_at", "updated_at", "category_id", "category_name")
VALUES (now(), now(), '2af327cc-5b82-4bdc-a77b-a5488973156e', 'ullam');
INSERT INTO p_category ("created_at", "updated_at", "category_id", "category_name")
VALUES (now(), now(), '4cddffcf-6161-49c5-919b-dc65cd8a0b65', 'numquam');
INSERT INTO p_category ("created_at", "updated_at", "category_id", "category_name")
VALUES (now(), now(), '8199ea61-42ea-4c8e-9ba0-b81210c398af', 'enim');
INSERT INTO p_category ("created_at", "updated_at", "category_id", "category_name")
VALUES (now(), now(), '68163774-d83f-4bcf-8725-6073fea76323', 'unde');
INSERT INTO p_category ("created_at", "updated_at", "category_id", "category_name")
VALUES (now(), now(), 'dbc8a00f-27cf-4338-ae3b-c6972a1d4f12', 'minus');
INSERT INTO p_category ("created_at", "updated_at", "category_id", "category_name")
VALUES (now(), now(), '97b7dc18-3bc7-42a5-9aec-8b39a4dec72a', 'rem');
INSERT INTO p_category ("created_at", "updated_at", "category_id", "category_name")
VALUES (now(), now(), '252181f8-4bfe-49fc-a387-60abcff54e5c', 'aperiam');
INSERT INTO p_category ("created_at", "updated_at", "category_id", "category_name")
VALUES (now(), now(), 'ac90e6bf-5f09-41b5-b951-5b21e1023669', 'nulla');
INSERT INTO p_category ("created_at", "updated_at", "category_id", "category_name")
VALUES (now(), now(), '1f6bb50d-e616-44b9-b069-e6f5113d6a60', 'odit');
INSERT INTO p_user ("created_at", "updated_at", "username", "email", "password", "nickname", "real_name",
                    "phone_number", "user_role")
VALUES (now(), now(), 'user_2dc4fa7757', 'user_e7efc7aa@example.com', 'a', 'nick_58b93f03', '박옥자', '010-2470-6633',
        'OWNER');
INSERT INTO p_store ("created_at", "updated_at", "store_id", "user_id", "region_id", "category_id", "store_name",
                     "description", "address", "phone_number", "min_order_amount", "store_accept_status")
VALUES (now(), now(), 'b7d53550-258b-4ced-a7e7-42b6510bd865', 1, '3bf1fca4-32b4-45b7-bf99-1822aefcec7a',
        '6530a750-89b7-44af-aebc-0e008fbeccd7', 'commodi', 'ea', '인천광역시 관악구 서초대가 610-34', '010-0354-7995', 16000,
        'PENDING');
INSERT INTO p_menu ("created_at", "updated_at", "menu_id", "store_id", "name", "description", "price", "is_hidden")
VALUES (now(), now(), 'd56e1bb1-a709-4772-b7da-a1b2b88af9c1', 'b7d53550-258b-4ced-a7e7-42b6510bd865', 'alias', 'eos',
        617, TRUE);
INSERT INTO p_menu ("created_at", "updated_at", "menu_id", "store_id", "name", "description", "price", "is_hidden")
VALUES (now(), now(), '215a1332-659e-4fa6-a701-08ae795786ab', 'b7d53550-258b-4ced-a7e7-42b6510bd865', 'architecto',
        'vero', 669, FALSE);
INSERT INTO p_menu ("created_at", "updated_at", "menu_id", "store_id", "name", "description", "price", "is_hidden")
VALUES (now(), now(), '48271d9f-a617-4a4a-9d10-df998f419896', 'b7d53550-258b-4ced-a7e7-42b6510bd865', 'vel',
        'laudantium', 205, TRUE);
INSERT INTO p_menu ("created_at", "updated_at", "menu_id", "store_id", "name", "description", "price", "is_hidden")
VALUES (now(), now(), 'fc889064-d11b-4986-8d8c-005c0d003b19', 'b7d53550-258b-4ced-a7e7-42b6510bd865', 'voluptatibus',
        'accusamus', 376, TRUE);
INSERT INTO p_menu ("created_at", "updated_at", "menu_id", "store_id", "name", "description", "price", "is_hidden")
VALUES (now(), now(), '924e444c-6aba-429b-a132-3c2079b8df2a', 'b7d53550-258b-4ced-a7e7-42b6510bd865', 'dignissimos',
        'exercitationem', 801, FALSE);
INSERT INTO p_menu ("created_at", "updated_at", "menu_id", "store_id", "name", "description", "price", "is_hidden")
VALUES (now(), now(), '418366d4-3dea-4db2-8c98-f25e49727ebd', 'b7d53550-258b-4ced-a7e7-42b6510bd865', 'repudiandae',
        'rem', 384, TRUE);
INSERT INTO p_menu ("created_at", "updated_at", "menu_id", "store_id", "name", "description", "price", "is_hidden")
VALUES (now(), now(), 'd682616f-881b-46eb-981b-10132a70b691', 'b7d53550-258b-4ced-a7e7-42b6510bd865', 'harum',
        'sapiente', 338, TRUE);
INSERT INTO p_menu ("created_at", "updated_at", "menu_id", "store_id", "name", "description", "price", "is_hidden")
VALUES (now(), now(), '786b737a-0c47-4c31-8d09-09dd4822736a', 'b7d53550-258b-4ced-a7e7-42b6510bd865', 'error',
        'repellat', 162, TRUE);
INSERT INTO p_user ("created_at", "updated_at", "username", "email", "password", "nickname", "real_name",
                    "phone_number", "user_role")
VALUES (now(), now(), 'user_3e17becfb8', 'user_8a37a446@example.com', 'a', 'nick_161ed099', '서재호', '010-8031-5144',
        'OWNER');
INSERT INTO p_store ("created_at", "updated_at", "store_id", "user_id", "region_id", "category_id", "store_name",
                     "description", "address", "phone_number", "min_order_amount", "store_accept_status")
VALUES (now(), now(), '4938a11b-c39b-4521-b568-fbc1058b4c11', 2, '81b7ce92-f3d3-4902-9651-1fe5bac6c26c',
        '2af327cc-5b82-4bdc-a77b-a5488973156e', 'nam', 'adipisci', '인천광역시 동구 테헤란1가 344', '010-8043-0405', 16000,
        'PENDING');
INSERT INTO p_menu ("created_at", "updated_at", "menu_id", "store_id", "name", "description", "price", "is_hidden")
VALUES (now(), now(), 'fb5dbc28-75b4-47e6-a610-01b052866519', '4938a11b-c39b-4521-b568-fbc1058b4c11', 'illum', 'autem',
        804, FALSE);
INSERT INTO p_menu ("created_at", "updated_at", "menu_id", "store_id", "name", "description", "price", "is_hidden")
VALUES (now(), now(), '44452cbe-b61d-46b0-a888-a11e69eea59c', '4938a11b-c39b-4521-b568-fbc1058b4c11', 'nulla', 'quae',
        224, FALSE);
INSERT INTO p_menu ("created_at", "updated_at", "menu_id", "store_id", "name", "description", "price", "is_hidden")
VALUES (now(), now(), '328f4218-0213-442e-855b-1cae0e3d706a', '4938a11b-c39b-4521-b568-fbc1058b4c11', 'officia', 'iure',
        560, FALSE);
INSERT INTO p_menu ("created_at", "updated_at", "menu_id", "store_id", "name", "description", "price", "is_hidden")
VALUES (now(), now(), '82cab45c-f788-49cd-8e1a-8ccd44d698c9', '4938a11b-c39b-4521-b568-fbc1058b4c11', 'in', 'quae', 750,
        TRUE);
INSERT INTO p_menu ("created_at", "updated_at", "menu_id", "store_id", "name", "description", "price", "is_hidden")
VALUES (now(), now(), 'be26fca4-fcef-4a87-80b2-c9ac10074f79', '4938a11b-c39b-4521-b568-fbc1058b4c11', 'deleniti',
        'facere', 106, TRUE);
INSERT INTO p_menu ("created_at", "updated_at", "menu_id", "store_id", "name", "description", "price", "is_hidden")
VALUES (now(), now(), '28b1f6bd-8899-4ae2-89a8-8c11343b2550', '4938a11b-c39b-4521-b568-fbc1058b4c11', 'voluptate', 'ex',
        47, FALSE);
INSERT INTO p_menu ("created_at", "updated_at", "menu_id", "store_id", "name", "description", "price", "is_hidden")
VALUES (now(), now(), 'b09d0da1-1c0c-47f3-b1a9-4f1445bdb31c', '4938a11b-c39b-4521-b568-fbc1058b4c11', 'sunt', 'et', 705,
        FALSE);
INSERT INTO p_menu ("created_at", "updated_at", "menu_id", "store_id", "name", "description", "price", "is_hidden")
VALUES (now(), now(), '3d3b953a-b2ac-4355-9c40-06805fbcee9d', '4938a11b-c39b-4521-b568-fbc1058b4c11', 'blanditiis',
        'quos', 55, TRUE);
INSERT INTO p_menu ("created_at", "updated_at", "menu_id", "store_id", "name", "description", "price", "is_hidden")
VALUES (now(), now(), 'a9711364-7971-4d88-8a85-790ae94c7e85', '4938a11b-c39b-4521-b568-fbc1058b4c11', 'vero', 'omnis',
        138, TRUE);
INSERT INTO p_menu ("created_at", "updated_at", "menu_id", "store_id", "name", "description", "price", "is_hidden")
VALUES (now(), now(), 'd7f19ce7-0108-47f6-9666-134c6e9a2614', '4938a11b-c39b-4521-b568-fbc1058b4c11', 'impedit', 'odio',
        870, TRUE);
INSERT INTO p_user ("created_at", "updated_at", "username", "email", "password", "nickname", "real_name",
                    "phone_number", "user_role")
VALUES (now(), now(), 'user_c46bed91c9', 'user_d6cd6d32@example.com', 'a', 'nick_08958b70', '김경숙', '010-8003-4935',
        'CUSTOMER');
INSERT INTO p_store ("created_at", "updated_at", "store_id", "user_id", "region_id", "category_id", "store_name",
                     "description", "address", "phone_number", "min_order_amount", "store_accept_status")
VALUES (now(), now(), '1f07f888-0b51-4218-969e-3806349fa04a', 3, '81b7ce92-f3d3-4902-9651-1fe5bac6c26c',
        '4cddffcf-6161-49c5-919b-dc65cd8a0b65', 'tempore', 'consequatur', '세종특별자치시 성동구 언주1가 지하837 (순옥이서면)',
        '010-4048-2108', 27000, 'APPROVE');
INSERT INTO p_menu ("created_at", "updated_at", "menu_id", "store_id", "name", "description", "price", "is_hidden")
VALUES (now(), now(), 'fcaea1bb-2c34-45fa-b7ba-4c534a629f9d', '1f07f888-0b51-4218-969e-3806349fa04a', 'deserunt',
        'atque', 669, TRUE);
INSERT INTO p_menu ("created_at", "updated_at", "menu_id", "store_id", "name", "description", "price", "is_hidden")
VALUES (now(), now(), '18c71ce9-241e-4ffd-8b26-6197dc2de155', '1f07f888-0b51-4218-969e-3806349fa04a', 'suscipit', 'cum',
        680, FALSE);
INSERT INTO p_menu ("created_at", "updated_at", "menu_id", "store_id", "name", "description", "price", "is_hidden")
VALUES (now(), now(), '15a26e09-6af4-40e9-ae21-5131d2404794', '1f07f888-0b51-4218-969e-3806349fa04a', 'sapiente',
        'quos', 784, FALSE);
INSERT INTO p_menu ("created_at", "updated_at", "menu_id", "store_id", "name", "description", "price", "is_hidden")
VALUES (now(), now(), '4c375cae-2ff2-445b-b627-9a09e166abcf', '1f07f888-0b51-4218-969e-3806349fa04a', 'nobis', 'sit',
        483, FALSE);
INSERT INTO p_menu ("created_at", "updated_at", "menu_id", "store_id", "name", "description", "price", "is_hidden")
VALUES (now(), now(), 'f9cee0a3-1f68-4269-9e0c-0dffd68644ce', '1f07f888-0b51-4218-969e-3806349fa04a', 'consequuntur',
        'molestiae', 588, FALSE);
INSERT INTO p_menu ("created_at", "updated_at", "menu_id", "store_id", "name", "description", "price", "is_hidden")
VALUES (now(), now(), '2263e421-41a2-46a3-a080-b202468bc280', '1f07f888-0b51-4218-969e-3806349fa04a', 'aspernatur',
        'vero', 359, TRUE);
INSERT INTO p_menu ("created_at", "updated_at", "menu_id", "store_id", "name", "description", "price", "is_hidden")
VALUES (now(), now(), '7c717eb2-b32f-486c-86c9-f0cf8d783729', '1f07f888-0b51-4218-969e-3806349fa04a', 'consectetur',
        'est', 373, FALSE);
INSERT INTO p_menu ("created_at", "updated_at", "menu_id", "store_id", "name", "description", "price", "is_hidden")
VALUES (now(), now(), 'a2b67de8-d683-4bc9-8a10-72dd2c35c24c', '1f07f888-0b51-4218-969e-3806349fa04a', 'eaque', 'nisi',
        320, TRUE);
INSERT INTO p_menu ("created_at", "updated_at", "menu_id", "store_id", "name", "description", "price", "is_hidden")
VALUES (now(), now(), '93d76e21-2b1c-4b98-9c3c-25c5f7355a16', '1f07f888-0b51-4218-969e-3806349fa04a', 'doloremque',
        'consectetur', 73, FALSE);
INSERT INTO p_menu ("created_at", "updated_at", "menu_id", "store_id", "name", "description", "price", "is_hidden")
VALUES (now(), now(), '2d71f370-0e6d-4a9b-b7d5-313f8d82a0a2', '1f07f888-0b51-4218-969e-3806349fa04a', 'nesciunt',
        'fugit', 879, TRUE);
INSERT INTO p_menu ("created_at", "updated_at", "menu_id", "store_id", "name", "description", "price", "is_hidden")
VALUES (now(), now(), '35ac5b57-2d19-445b-a097-f426fc25b68f', '1f07f888-0b51-4218-969e-3806349fa04a', 'eius',
        'excepturi', 782, TRUE);
INSERT INTO p_menu ("created_at", "updated_at", "menu_id", "store_id", "name", "description", "price", "is_hidden")
VALUES (now(), now(), '23fe4f24-afc6-4a79-82bb-6e0d3306f68b', '1f07f888-0b51-4218-969e-3806349fa04a', 'odit', 'a', 735,
        FALSE);
INSERT INTO p_user ("created_at", "updated_at", "username", "email", "password", "nickname", "real_name",
                    "phone_number", "user_role")
VALUES (now(), now(), 'user_4a9cfa06e9', 'user_c3659eac@example.com', 'a', 'nick_17fdaf2a', '윤현정', '010-0970-2695',
        'OWNER');
INSERT INTO p_store ("created_at", "updated_at", "store_id", "user_id", "region_id", "category_id", "store_name",
                     "description", "address", "phone_number", "min_order_amount", "store_accept_status")
VALUES (now(), now(), '92d09763-aaaa-438e-822d-8f3ed60d82fd', 4, '92186ef3-6d99-471a-8849-d3e1e2528a34',
        '8199ea61-42ea-4c8e-9ba0-b81210c398af', 'iste', 'dolor', '인천광역시 강동구 도산대152로 804-24 (정수황안마을)', '010-8147-0884',
        19000, 'APPROVE');
INSERT INTO p_menu ("created_at", "updated_at", "menu_id", "store_id", "name", "description", "price", "is_hidden")
VALUES (now(), now(), '3fbbe89b-333d-4e09-bbdc-d84bf229d560', '92d09763-aaaa-438e-822d-8f3ed60d82fd', 'explicabo',
        'aspernatur', 250, FALSE);
INSERT INTO p_menu ("created_at", "updated_at", "menu_id", "store_id", "name", "description", "price", "is_hidden")
VALUES (now(), now(), 'ae7b8d14-9cd4-4ea7-8c90-6bc9bb70cd66', '92d09763-aaaa-438e-822d-8f3ed60d82fd', 'eaque', 'at',
        117, TRUE);
INSERT INTO p_menu ("created_at", "updated_at", "menu_id", "store_id", "name", "description", "price", "is_hidden")
VALUES (now(), now(), 'f60094c7-be71-4f7e-aec8-65ea1ec030ce', '92d09763-aaaa-438e-822d-8f3ed60d82fd', 'qui',
        'architecto', 489, TRUE);
INSERT INTO p_menu ("created_at", "updated_at", "menu_id", "store_id", "name", "description", "price", "is_hidden")
VALUES (now(), now(), '67ee7a6e-deee-4317-a106-3d429f65124a', '92d09763-aaaa-438e-822d-8f3ed60d82fd', 'optio',
        'cupiditate', 93, TRUE);
INSERT INTO p_menu ("created_at", "updated_at", "menu_id", "store_id", "name", "description", "price", "is_hidden")
VALUES (now(), now(), '14a6d032-65be-4b23-9afc-7b1950fd2e05', '92d09763-aaaa-438e-822d-8f3ed60d82fd', 'ut', 'ratione',
        137, FALSE);
INSERT INTO p_menu ("created_at", "updated_at", "menu_id", "store_id", "name", "description", "price", "is_hidden")
VALUES (now(), now(), '65c9595f-a637-4114-829b-3356243b00b7', '92d09763-aaaa-438e-822d-8f3ed60d82fd', 'nihil', 'eius',
        224, TRUE);
INSERT INTO p_menu ("created_at", "updated_at", "menu_id", "store_id", "name", "description", "price", "is_hidden")
VALUES (now(), now(), 'b8f0447b-c1e9-43f8-a2d3-842cd4acab49', '92d09763-aaaa-438e-822d-8f3ed60d82fd', 'enim',
        'inventore', 935, TRUE);
INSERT INTO p_user ("created_at", "updated_at", "username", "email", "password", "nickname", "real_name",
                    "phone_number", "user_role")
VALUES (now(), now(), 'user_807b2d69a5', 'user_48dc905b@example.com', 'a', 'nick_70289ace', '배건우', '010-4010-4738',
        'OWNER');
INSERT INTO p_store ("created_at", "updated_at", "store_id", "user_id", "region_id", "category_id", "store_name",
                     "description", "address", "phone_number", "min_order_amount", "store_accept_status")
VALUES (now(), now(), '62d97f82-8757-459f-b0a2-96e0024aeff6', 5, '3bf1fca4-32b4-45b7-bf99-1822aefcec7a',
        '68163774-d83f-4bcf-8725-6073fea76323', 'totam', 'eaque', '대전광역시 동구 도산대7가 310-89', '010-3310-0841', 29000,
        'REJECT');
INSERT INTO p_menu ("created_at", "updated_at", "menu_id", "store_id", "name", "description", "price", "is_hidden")
VALUES (now(), now(), '04bc895e-cfbe-4607-b14f-7f56d7b60b54', '62d97f82-8757-459f-b0a2-96e0024aeff6', 'quaerat', 'odio',
        190, FALSE);
INSERT INTO p_menu ("created_at", "updated_at", "menu_id", "store_id", "name", "description", "price", "is_hidden")
VALUES (now(), now(), '8b185506-f846-4034-bf28-d12ff9aa2cb1', '62d97f82-8757-459f-b0a2-96e0024aeff6', 'animi',
        'maiores', 544, TRUE);
INSERT INTO p_menu ("created_at", "updated_at", "menu_id", "store_id", "name", "description", "price", "is_hidden")
VALUES (now(), now(), 'db477d2f-e6da-4931-a061-3fecae1ea3bd', '62d97f82-8757-459f-b0a2-96e0024aeff6', 'magni', 'fugit',
        705, TRUE);
INSERT INTO p_menu ("created_at", "updated_at", "menu_id", "store_id", "name", "description", "price", "is_hidden")
VALUES (now(), now(), 'da7bc119-a133-42f1-9a01-b6b85dfcee4c', '62d97f82-8757-459f-b0a2-96e0024aeff6', 'repellendus',
        'laborum', 406, TRUE);
INSERT INTO p_menu ("created_at", "updated_at", "menu_id", "store_id", "name", "description", "price", "is_hidden")
VALUES (now(), now(), 'd942d7f7-d1ae-47f4-a440-869719c7dddd', '62d97f82-8757-459f-b0a2-96e0024aeff6', 'qui', 'natus',
        788, TRUE);
INSERT INTO p_menu ("created_at", "updated_at", "menu_id", "store_id", "name", "description", "price", "is_hidden")
VALUES (now(), now(), '7427910a-c32f-4078-affb-1d8b4fbb9356', '62d97f82-8757-459f-b0a2-96e0024aeff6', 'optio', 'vel',
        873, TRUE);
INSERT INTO p_user ("created_at", "updated_at", "username", "email", "password", "nickname", "real_name",
                    "phone_number", "user_role")
VALUES (now(), now(), 'user_4861b4217c', 'user_e92878bc@example.com', 'a', 'nick_f7b3ef20', '장수진', '010-1280-8689',
        'OWNER');
INSERT INTO p_store ("created_at", "updated_at", "store_id", "user_id", "region_id", "category_id", "store_name",
                     "description", "address", "phone_number", "min_order_amount", "store_accept_status")
VALUES (now(), now(), '2fd5329e-7ab2-4767-b16f-3066623729a2', 6, '8adefc6f-9f7b-433b-b53c-b2fd3deb82cd',
        'dbc8a00f-27cf-4338-ae3b-c6972a1d4f12', 'ea', 'deleniti', '전라북도 파주시 반포대로 236 (상현우읍)', '010-3251-8093', 30000,
        'APPROVE');
INSERT INTO p_menu ("created_at", "updated_at", "menu_id", "store_id", "name", "description", "price", "is_hidden")
VALUES (now(), now(), 'eb216d07-899a-48c8-94a2-39a88421c693', '2fd5329e-7ab2-4767-b16f-3066623729a2', 'itaque', 'autem',
        351, TRUE);
INSERT INTO p_menu ("created_at", "updated_at", "menu_id", "store_id", "name", "description", "price", "is_hidden")
VALUES (now(), now(), '3683e72f-c624-496b-b786-47183e2a7b5a', '2fd5329e-7ab2-4767-b16f-3066623729a2', 'et',
        'consectetur', 190, FALSE);
INSERT INTO p_menu ("created_at", "updated_at", "menu_id", "store_id", "name", "description", "price", "is_hidden")
VALUES (now(), now(), '127308ab-606f-46db-be66-d9c900fa2f1e', '2fd5329e-7ab2-4767-b16f-3066623729a2', 'voluptate',
        'doloremque', 413, FALSE);
INSERT INTO p_menu ("created_at", "updated_at", "menu_id", "store_id", "name", "description", "price", "is_hidden")
VALUES (now(), now(), 'e2ba804b-b17b-42bd-97a5-1e653e4a800e', '2fd5329e-7ab2-4767-b16f-3066623729a2', 'neque', 'ipsum',
        10, FALSE);
INSERT INTO p_menu ("created_at", "updated_at", "menu_id", "store_id", "name", "description", "price", "is_hidden")
VALUES (now(), now(), 'b1b5d1a0-20fb-4365-955e-764ad3254393', '2fd5329e-7ab2-4767-b16f-3066623729a2', 'temporibus',
        'accusamus', 974, FALSE);
INSERT INTO p_menu ("created_at", "updated_at", "menu_id", "store_id", "name", "description", "price", "is_hidden")
VALUES (now(), now(), 'e85e2bcd-7ff3-4e98-a691-52dc186c33ae', '2fd5329e-7ab2-4767-b16f-3066623729a2', 'ducimus',
        'animi', 364, FALSE);
INSERT INTO p_menu ("created_at", "updated_at", "menu_id", "store_id", "name", "description", "price", "is_hidden")
VALUES (now(), now(), 'adadccec-ee16-4272-9eb9-f2ad72dfe250', '2fd5329e-7ab2-4767-b16f-3066623729a2', 'aliquid',
        'numquam', 403, FALSE);
INSERT INTO p_menu ("created_at", "updated_at", "menu_id", "store_id", "name", "description", "price", "is_hidden")
VALUES (now(), now(), '38a5e6f4-02c4-4646-b1e2-b3c70cb98bee', '2fd5329e-7ab2-4767-b16f-3066623729a2', 'placeat', 'non',
        787, FALSE);
INSERT INTO p_menu ("created_at", "updated_at", "menu_id", "store_id", "name", "description", "price", "is_hidden")
VALUES (now(), now(), '42117e0f-951f-4ef1-8639-6cb76cfec6ae', '2fd5329e-7ab2-4767-b16f-3066623729a2', 'facere', 'quae',
        650, TRUE);
INSERT INTO p_menu ("created_at", "updated_at", "menu_id", "store_id", "name", "description", "price", "is_hidden")
VALUES (now(), now(), 'c27d9469-e321-4a10-991f-6a3b99c91d82', '2fd5329e-7ab2-4767-b16f-3066623729a2', 'corrupti',
        'cumque', 550, FALSE);
INSERT INTO p_menu ("created_at", "updated_at", "menu_id", "store_id", "name", "description", "price", "is_hidden")
VALUES (now(), now(), '8a8af5f2-758f-4dab-b4cc-0ca10cc77f4a', '2fd5329e-7ab2-4767-b16f-3066623729a2', 'magnam',
        'reprehenderit', 445, TRUE);
INSERT INTO p_menu ("created_at", "updated_at", "menu_id", "store_id", "name", "description", "price", "is_hidden")
VALUES (now(), now(), '644fe9ef-5c70-4797-b533-c59979530964', '2fd5329e-7ab2-4767-b16f-3066623729a2', 'nemo', 'dolor',
        535, TRUE);
INSERT INTO p_menu ("created_at", "updated_at", "menu_id", "store_id", "name", "description", "price", "is_hidden")
VALUES (now(), now(), 'f82209e2-8521-4e9a-879d-9229d5bde34e', '2fd5329e-7ab2-4767-b16f-3066623729a2', 'maiores',
        'dignissimos', 529, TRUE);
INSERT INTO p_menu ("created_at", "updated_at", "menu_id", "store_id", "name", "description", "price", "is_hidden")
VALUES (now(), now(), '5cd52e68-bcbd-4dd2-adf1-ef8a68411b25', '2fd5329e-7ab2-4767-b16f-3066623729a2', 'illum', 'aut',
        877, TRUE);
INSERT INTO p_menu ("created_at", "updated_at", "menu_id", "store_id", "name", "description", "price", "is_hidden")
VALUES (now(), now(), '8c5b678f-da71-45e4-b828-c931e897a040', '2fd5329e-7ab2-4767-b16f-3066623729a2', 'odio',
        'perspiciatis', 443, FALSE);
INSERT INTO p_user ("created_at", "updated_at", "username", "email", "password", "nickname", "real_name",
                    "phone_number", "user_role")
VALUES (now(), now(), 'user_4f52709c7e', 'user_6ee108e2@example.com', 'a', 'nick_f760de76', '주경자', '010-7862-7120',
        'MANAGER');
INSERT INTO p_store ("created_at", "updated_at", "store_id", "user_id", "region_id", "category_id", "store_name",
                     "description", "address", "phone_number", "min_order_amount", "store_accept_status")
VALUES (now(), now(), '398dcde5-5b2b-4432-acce-150682c15e6f', 7, '8adefc6f-9f7b-433b-b53c-b2fd3deb82cd',
        '97b7dc18-3bc7-42a5-9aec-8b39a4dec72a', 'facere', 'possimus', '부산광역시 동대문구 오금56가 242 (지현황리)', '010-6120-3723',
        21000, 'REJECT');
INSERT INTO p_menu ("created_at", "updated_at", "menu_id", "store_id", "name", "description", "price", "is_hidden")
VALUES (now(), now(), 'd45d0a97-c615-4eee-955f-6a80ca1f7eb4', '398dcde5-5b2b-4432-acce-150682c15e6f', 'maiores',
        'dignissimos', 868, TRUE);
INSERT INTO p_menu ("created_at", "updated_at", "menu_id", "store_id", "name", "description", "price", "is_hidden")
VALUES (now(), now(), '2a4ba321-817d-45a6-b951-2407d173386e', '398dcde5-5b2b-4432-acce-150682c15e6f', 'corporis',
        'laboriosam', 813, TRUE);
INSERT INTO p_menu ("created_at", "updated_at", "menu_id", "store_id", "name", "description", "price", "is_hidden")
VALUES (now(), now(), '11829976-f1ad-4b78-84c6-d1df58436d08', '398dcde5-5b2b-4432-acce-150682c15e6f', 'aut', 'libero',
        867, FALSE);
INSERT INTO p_menu ("created_at", "updated_at", "menu_id", "store_id", "name", "description", "price", "is_hidden")
VALUES (now(), now(), '004a26c2-6eae-434a-bd80-e9b727b3f5dd', '398dcde5-5b2b-4432-acce-150682c15e6f', 'nemo',
        'laudantium', 136, FALSE);
INSERT INTO p_menu ("created_at", "updated_at", "menu_id", "store_id", "name", "description", "price", "is_hidden")
VALUES (now(), now(), '134b790d-9dfc-4084-934f-3d8470cb1e22', '398dcde5-5b2b-4432-acce-150682c15e6f', 'esse',
        'perferendis', 30, TRUE);
INSERT INTO p_user ("created_at", "updated_at", "username", "email", "password", "nickname", "real_name",
                    "phone_number", "user_role")
VALUES (now(), now(), 'user_e827bd7640', 'user_89ebc761@example.com', 'a', 'nick_f7b7a542', '류주원', '010-7173-4988',
        'MANAGER');
INSERT INTO p_store ("created_at", "updated_at", "store_id", "user_id", "region_id", "category_id", "store_name",
                     "description", "address", "phone_number", "min_order_amount", "store_accept_status")
VALUES (now(), now(), '9841bb89-8ca9-4cc1-808c-08185f0b87b7', 8, '81b7ce92-f3d3-4902-9651-1fe5bac6c26c',
        '252181f8-4bfe-49fc-a387-60abcff54e5c', 'sunt', 'possimus', '광주광역시 관악구 학동길 826 (은영박리)', '010-7457-3127', 12000,
        'PENDING');
INSERT INTO p_menu ("created_at", "updated_at", "menu_id", "store_id", "name", "description", "price", "is_hidden")
VALUES (now(), now(), '26092812-a10c-4a17-b442-acf4517d8fef', '9841bb89-8ca9-4cc1-808c-08185f0b87b7', 'fugit', 'vero',
        411, TRUE);
INSERT INTO p_menu ("created_at", "updated_at", "menu_id", "store_id", "name", "description", "price", "is_hidden")
VALUES (now(), now(), '6e75f752-7d00-4694-b1d0-05fabf34cc6d', '9841bb89-8ca9-4cc1-808c-08185f0b87b7', 'earum', 'eos',
        527, FALSE);
INSERT INTO p_menu ("created_at", "updated_at", "menu_id", "store_id", "name", "description", "price", "is_hidden")
VALUES (now(), now(), '98678724-4642-427f-a4a9-e60184f6073c', '9841bb89-8ca9-4cc1-808c-08185f0b87b7', 'animi',
        'voluptatum', 78, FALSE);
INSERT INTO p_menu ("created_at", "updated_at", "menu_id", "store_id", "name", "description", "price", "is_hidden")
VALUES (now(), now(), '2c18b250-3d70-405c-b818-780653f6b8d8', '9841bb89-8ca9-4cc1-808c-08185f0b87b7', 'perspiciatis',
        'quos', 988, TRUE);
INSERT INTO p_menu ("created_at", "updated_at", "menu_id", "store_id", "name", "description", "price", "is_hidden")
VALUES (now(), now(), '77d5c345-8595-40e3-9ac2-f6f36d67e69d', '9841bb89-8ca9-4cc1-808c-08185f0b87b7', 'unde', 'sequi',
        47, TRUE);
INSERT INTO p_menu ("created_at", "updated_at", "menu_id", "store_id", "name", "description", "price", "is_hidden")
VALUES (now(), now(), '682a74ae-a676-4d82-98a2-de3c1827dc5c', '9841bb89-8ca9-4cc1-808c-08185f0b87b7', 'tempore', 'ab',
        810, TRUE);
INSERT INTO p_menu ("created_at", "updated_at", "menu_id", "store_id", "name", "description", "price", "is_hidden")
VALUES (now(), now(), '90921fd6-1226-46e7-85f7-ea3d00ede43f', '9841bb89-8ca9-4cc1-808c-08185f0b87b7', 'dignissimos',
        'quidem', 447, TRUE);
INSERT INTO p_menu ("created_at", "updated_at", "menu_id", "store_id", "name", "description", "price", "is_hidden")
VALUES (now(), now(), '57eda1b5-dadf-4f33-81aa-ff3e1156d79a', '9841bb89-8ca9-4cc1-808c-08185f0b87b7', 'consectetur',
        'impedit', 917, FALSE);
INSERT INTO p_menu ("created_at", "updated_at", "menu_id", "store_id", "name", "description", "price", "is_hidden")
VALUES (now(), now(), '7233cc2f-24a9-431d-8641-c126687f4029', '9841bb89-8ca9-4cc1-808c-08185f0b87b7', 'quia',
        'similique', 689, TRUE);
INSERT INTO p_menu ("created_at", "updated_at", "menu_id", "store_id", "name", "description", "price", "is_hidden")
VALUES (now(), now(), 'ab7332a8-bc9c-4ca6-b9ac-3a71b88f1f89', '9841bb89-8ca9-4cc1-808c-08185f0b87b7', 'sint',
        'incidunt', 625, FALSE);
INSERT INTO p_menu ("created_at", "updated_at", "menu_id", "store_id", "name", "description", "price", "is_hidden")
VALUES (now(), now(), '35e40631-b5a9-4a6e-a48e-e1351caf6d7b', '9841bb89-8ca9-4cc1-808c-08185f0b87b7', 'eos',
        'consectetur', 681, TRUE);
INSERT INTO p_menu ("created_at", "updated_at", "menu_id", "store_id", "name", "description", "price", "is_hidden")
VALUES (now(), now(), 'b828ac1e-ed6f-40a2-9667-030d59fc63a2', '9841bb89-8ca9-4cc1-808c-08185f0b87b7', 'ex', 'eius', 595,
        FALSE);
INSERT INTO p_menu ("created_at", "updated_at", "menu_id", "store_id", "name", "description", "price", "is_hidden")
VALUES (now(), now(), 'ce570b20-098d-48f9-b764-65865d2a9be5', '9841bb89-8ca9-4cc1-808c-08185f0b87b7', 'sapiente',
        'quibusdam', 459, TRUE);
INSERT INTO p_menu ("created_at", "updated_at", "menu_id", "store_id", "name", "description", "price", "is_hidden")
VALUES (now(), now(), '14af28c1-ae80-41be-8732-72c9689301e3', '9841bb89-8ca9-4cc1-808c-08185f0b87b7', 'molestias',
        'repudiandae', 11, TRUE);
INSERT INTO p_menu ("created_at", "updated_at", "menu_id", "store_id", "name", "description", "price", "is_hidden")
VALUES (now(), now(), 'faa0a8e9-7b9f-47f7-827c-c967e8c3b830', '9841bb89-8ca9-4cc1-808c-08185f0b87b7', 'ratione',
        'ducimus', 754, FALSE);
INSERT INTO p_user ("created_at", "updated_at", "username", "email", "password", "nickname", "real_name",
                    "phone_number", "user_role")
VALUES (now(), now(), 'user_babb7cb874', 'user_d111ca4c@example.com', 'a', 'nick_e7cf559d', '송정웅', '010-1133-1037',
        'MANAGER');
INSERT INTO p_store ("created_at", "updated_at", "store_id", "user_id", "region_id", "category_id", "store_name",
                     "description", "address", "phone_number", "min_order_amount", "store_accept_status")
VALUES (now(), now(), '51766445-cfc7-47db-ab1a-5d84999e0edf', 9, '8adefc6f-9f7b-433b-b53c-b2fd3deb82cd',
        'ac90e6bf-5f09-41b5-b951-5b21e1023669', 'rem', 'consectetur', '울산광역시 서구 강남대501길 925-50 (영숙최오면)',
        '010-8743-2850', 16000, 'APPROVE');
INSERT INTO p_menu ("created_at", "updated_at", "menu_id", "store_id", "name", "description", "price", "is_hidden")
VALUES (now(), now(), '41b0806a-d20c-489b-a8a5-a5ac526703e2', '51766445-cfc7-47db-ab1a-5d84999e0edf', 'placeat',
        'beatae', 451, FALSE);
INSERT INTO p_menu ("created_at", "updated_at", "menu_id", "store_id", "name", "description", "price", "is_hidden")
VALUES (now(), now(), '768c12a7-c715-4978-a2fb-9069ede9aab1', '51766445-cfc7-47db-ab1a-5d84999e0edf', 'non',
        'perspiciatis', 857, FALSE);
INSERT INTO p_menu ("created_at", "updated_at", "menu_id", "store_id", "name", "description", "price", "is_hidden")
VALUES (now(), now(), '4ed2a4d9-dabb-4c15-881a-7d64632fee39', '51766445-cfc7-47db-ab1a-5d84999e0edf', 'illum',
        'cupiditate', 170, TRUE);
INSERT INTO p_menu ("created_at", "updated_at", "menu_id", "store_id", "name", "description", "price", "is_hidden")
VALUES (now(), now(), 'f774f57e-6c93-4120-8503-7a3521738a11', '51766445-cfc7-47db-ab1a-5d84999e0edf', 'officia', 'eum',
        222, TRUE);
INSERT INTO p_menu ("created_at", "updated_at", "menu_id", "store_id", "name", "description", "price", "is_hidden")
VALUES (now(), now(), 'e67ce7d9-ff8c-4b3c-ab47-59a19a5f2e9a', '51766445-cfc7-47db-ab1a-5d84999e0edf', 'nihil',
        'recusandae', 953, TRUE);
INSERT INTO p_menu ("created_at", "updated_at", "menu_id", "store_id", "name", "description", "price", "is_hidden")
VALUES (now(), now(), '216caf44-d183-41cc-8707-3d9a9f2e2dcb', '51766445-cfc7-47db-ab1a-5d84999e0edf', 'tempora',
        'quaerat', 157, TRUE);
INSERT INTO p_menu ("created_at", "updated_at", "menu_id", "store_id", "name", "description", "price", "is_hidden")
VALUES (now(), now(), '655d3aee-c64e-4940-8ea2-9c37a28fa2e6', '51766445-cfc7-47db-ab1a-5d84999e0edf', 'quisquam',
        'eaque', 72, FALSE);
INSERT INTO p_menu ("created_at", "updated_at", "menu_id", "store_id", "name", "description", "price", "is_hidden")
VALUES (now(), now(), '8f0e1336-e2c3-4ab3-9c61-a9fe4bf05de3', '51766445-cfc7-47db-ab1a-5d84999e0edf', 'nihil', 'magnam',
        363, TRUE);
INSERT INTO p_user ("created_at", "updated_at", "username", "email", "password", "nickname", "real_name",
                    "phone_number", "user_role")
VALUES (now(), now(), 'user_86729caa26', 'user_78abce98@example.com', 'a', 'nick_06de0357', '최영미', '010-9636-4640',
        'MANAGER');
INSERT INTO p_store ("created_at", "updated_at", "store_id", "user_id", "region_id", "category_id", "store_name",
                     "description", "address", "phone_number", "min_order_amount", "store_accept_status")
VALUES (now(), now(), '00c103d0-e130-445e-99a7-b9ab0b865560', 10, '92186ef3-6d99-471a-8849-d3e1e2528a34',
        '1f6bb50d-e616-44b9-b069-e6f5113d6a60', 'similique', 'sunt', '울산광역시 구로구 테헤란길 944 (동현이읍)', '010-4377-6628',
        28000, 'APPROVE');
INSERT INTO p_menu ("created_at", "updated_at", "menu_id", "store_id", "name", "description", "price", "is_hidden")
VALUES (now(), now(), '1b79597e-4d95-4a26-9b06-3e6d4a602df2', '00c103d0-e130-445e-99a7-b9ab0b865560', 'deserunt',
        'impedit', 252, FALSE);
INSERT INTO p_menu ("created_at", "updated_at", "menu_id", "store_id", "name", "description", "price", "is_hidden")
VALUES (now(), now(), '6ca79b81-f8dc-4cbb-8cc5-a5fa350a094e', '00c103d0-e130-445e-99a7-b9ab0b865560', 'repellat',
        'iste', 146, TRUE);
INSERT INTO p_menu ("created_at", "updated_at", "menu_id", "store_id", "name", "description", "price", "is_hidden")
VALUES (now(), now(), 'de37df35-f3a3-4978-95c4-5954c6c5044a', '00c103d0-e130-445e-99a7-b9ab0b865560', 'eveniet', 'quod',
        160, FALSE);
INSERT INTO p_menu ("created_at", "updated_at", "menu_id", "store_id", "name", "description", "price", "is_hidden")
VALUES (now(), now(), 'dea31c0d-baa4-4df6-8bc3-56f28b5d1182', '00c103d0-e130-445e-99a7-b9ab0b865560', 'id',
        'consequatur', 821, TRUE);
INSERT INTO p_menu ("created_at", "updated_at", "menu_id", "store_id", "name", "description", "price", "is_hidden")
VALUES (now(), now(), '8ef509d3-d0fd-4d26-bb6c-40c37639f667', '00c103d0-e130-445e-99a7-b9ab0b865560', 'possimus',
        'temporibus', 226, FALSE);
INSERT INTO p_menu ("created_at", "updated_at", "menu_id", "store_id", "name", "description", "price", "is_hidden")
VALUES (now(), now(), '07353bad-f6b3-4b1f-8955-a89910c96b0c', '00c103d0-e130-445e-99a7-b9ab0b865560', 'ducimus', 'modi',
        645, TRUE);
INSERT INTO p_menu ("created_at", "updated_at", "menu_id", "store_id", "name", "description", "price", "is_hidden")
VALUES (now(), now(), 'dd9c649b-a8fd-4fed-9be7-505e59ddcc89', '00c103d0-e130-445e-99a7-b9ab0b865560', 'illo', 'ea', 967,
        FALSE);
INSERT INTO public.p_orders (total_price, created_at, created_by, deleted_at, deleted_by, updated_at, updated_by,
                             user_id, orders_id, store_id, order_channel, order_status, receipt_method, payment_method,
                             delivery_address, request_message)
VALUES (21000, '2025-08-01 09:24:54.980000', null, null, null, '2025-08-01 09:24:54.980000', null, 11,
        '7e5a2c89-1a11-4a2e-875c-b293bbf991dd', 'b7d53550-258b-4ced-a7e7-42b6510bd865', 'ONLINE', 'PENDING', 'DELIVERY',
        'CREDIT_CARD', '서울특별시 중구 을지로 100', null)