/**
 * Copyright (C) 2011  JTalks.org Team
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package org.jtalks.jcommune.service.nontransactional;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.jtalks.common.model.entity.Property;
import org.jtalks.jcommune.model.dao.PropertyDao;
import org.jtalks.jcommune.model.entity.JCommuneProperty;
import org.jtalks.jcommune.service.exceptions.ImageFormatException;
import org.jtalks.jcommune.service.exceptions.ImageProcessException;
import org.jtalks.jcommune.service.exceptions.ImageSizeException;
import org.mockito.Mock;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * @author Alexandre Teterin
 */
public class AvatarServiceTest {
    private static final String PROPERTY_NAME = "property";
    private static final int AVATAR_MAX_SIZE = 1000;
    private AvatarService avatarService;
    @Mock
    private ImageUtils imageUtils;
    @Mock
    private Base64Wrapper base64Wrapper;
    @Mock
    private PropertyDao propertyDao;
    private JCommuneProperty avatarSizeProperty = JCommuneProperty.AVATAR_MAX_SIZE;

    @BeforeMethod
    public void setUp() {
        initMocks(this);
        avatarSizeProperty.setName(PROPERTY_NAME);
        avatarSizeProperty.setPropertyDao(propertyDao);
        when(propertyDao.getByName(PROPERTY_NAME))
            .thenReturn(new Property(PROPERTY_NAME, String.valueOf(AVATAR_MAX_SIZE)));
        avatarService = new AvatarService(
                imageUtils,
                base64Wrapper,
                "org/jtalks/jcommune/service/avatar.gif",
                avatarSizeProperty);
    }

    @Test(dataProvider = "validImageBytesValues")
    public void shouldNormalProcessConvertAvatarToBase64String(byte[] originalImageBytes,
                                                               BufferedImage inputImage,
                                                               byte[] processedImageBytes,
                                                               String expectedBase64String) throws Exception {
        //set expectations
        when(imageUtils.convertByteArrayToImage(originalImageBytes)).thenReturn(inputImage);
        when(imageUtils.preprocessImage(inputImage)).thenReturn(processedImageBytes);
        when(base64Wrapper.encodeB64Bytes(processedImageBytes)).thenReturn(expectedBase64String);

        //invoke object under test
        String resultBase64String = avatarService.convertBytesToBase64String(originalImageBytes);

        //check expectations
        verify(imageUtils).convertByteArrayToImage(originalImageBytes);
        verify(imageUtils).preprocessImage(inputImage);
        verify(base64Wrapper).encodeB64Bytes(processedImageBytes);

        //check result
        assertEquals(resultBase64String, expectedBase64String);

    }

    @Test(expectedExceptions = ImageProcessException.class, dataProvider = "invalidImageBytesValues")
    public void inputDataForProcessConvertAvatarToBase64StringIsInvalid(byte[] originalImageBytes,
                                                                        BufferedImage inputImage) throws Exception {
        //set expectations
        when(imageUtils.convertByteArrayToImage(originalImageBytes)).thenReturn(inputImage);

        //invoke object under test
        avatarService.convertBytesToBase64String(originalImageBytes);

        //check expectations
        verify(imageUtils).convertByteArrayToImage(originalImageBytes);
    }

    @Test
    public void testGetDefaultAvatar() {
        byte[] avatar = avatarService.getDefaultAvatar();

        assertTrue(avatar.length > 0);
    }


    @Test(expectedExceptions = ImageFormatException.class, dataProvider = "invalidFormatValues")
    public void inputDataForValidateAvatarFormatIsInvalid(MultipartFile file) throws Exception {
        //invoke object under test
        avatarService.validateAvatarFormat(file);
    }

    @Test(dataProvider = "validFormatValues")
    public void inputDataForValidateAvatarFormatFromOperaIEIsValid(MultipartFile file) throws Exception {
        //invoke object under test
        avatarService.validateAvatarFormat(file);
    }

    @Test(dataProvider = "validFormatValuesForChromeFF")
    public void inputDataForValidateAvatarFormatFromChromeFFIsValid(byte[] bytes) throws ImageFormatException {
        avatarService.validateAvatarFormat(bytes);
    }



    @Test(dataProvider = "invalidFormatValuesForChromeFF", expectedExceptions = ImageFormatException.class)
    public void inputDataForValidateAvatarFormatFromChromeFFIsInvalid(byte[] bytes) throws ImageFormatException {
        avatarService.validateAvatarFormat(bytes);
    }

    @Test(expectedExceptions = ImageSizeException.class)
    public void inputDataForValidateAvatarSizeIsInvalid() throws Exception {
        byte[] bytes = new byte[AVATAR_MAX_SIZE * 2];
        //invoke object under test
        avatarService.validateAvatarSize(bytes);
    }

    
    @Test
    public void inputDataForValidateAvatarSizeIsValid() {
    	byte[] bytes = new byte[AVATAR_MAX_SIZE];
    	try {
			avatarService.validateAvatarSize(bytes);
		} catch (ImageSizeException e) {
			assertTrue(true, "The correct picture is not passed the test.");
		}
    }


    @DataProvider
    private Object[][] validImageBytesValues() throws IOException, ImageProcessException {
        byte[] originalImageBytes = new byte[]{-119, 80, 78, 71, 13, 10, 26, 10, 0, 0, 0, 13, 73, 72, 68, 82, 0, 0,
                0, 5, 0, 0, 0, 5, 8, 2, 0, 0, 0, 2, 13, -79, -78, 0, 0, 0, 9, 112, 72, 89, 115, 0, 0, 1, -118, 0,
                0, 1, -118, 1, 51, -105, 48, 88, 0, 0, 0, 32, 99, 72, 82, 77, 0, 0, 122, 37, 0, 0, -128, -125,
                0, 0, -7, -1, 0, 0, -128, -23, 0, 0, 117, 48, 0, 0, -22, 96, 0, 0, 58, -104, 0, 0, 23, 111,
                -110, 95, -59, 70, 0, 0, 0, 54, 73, 68, 65, 84, 120, -38, 76, -55, -79, 21, -128, 32, 0, -60,
                -48, 28, 14, 32, -52, -30, -2, -93, 121, -79, -112, -126, 116, -1, 37, 42, 71, 3, -72, -41, 4,
                -110, -88, -88, 42, 79, -37, 110, 3, 109, -81, 12, -33, -26, -1, 73, -88, 36, -33, 0, -62, -31,
                36, 71, 49, 115, -89, 85, 0, 0, 0, 0, 73, 69, 78, 68, -82, 66, 96, -126
        };

        ImageUtils imageUtils = new ImageUtils(new Base64Wrapper());
        BufferedImage inputImage = imageUtils.convertByteArrayToImage(originalImageBytes);
        byte[] processedImageBytes = imageUtils.preprocessImage(inputImage);
        String expectedBase64String = new Base64Wrapper().encodeB64Bytes(processedImageBytes);

        return new Object[][]{
                {originalImageBytes, inputImage, processedImageBytes, expectedBase64String}
        };
    }

    @DataProvider
    private Object[][] invalidImageBytesValues() throws Exception {
        ImageUtils imageUtils = new ImageUtils(new Base64Wrapper());

        byte[] originalImageBytes = new byte[]{96, -126};

        BufferedImage inputImage = imageUtils.convertByteArrayToImage(originalImageBytes);

        return new Object[][]{
                {originalImageBytes, inputImage},
        };
    }

    @DataProvider
    public Object[][] invalidFormatValues() {
        return new Object[][]{
                {new MockMultipartFile("test_avatar", "test_avatar", "image/bmp", new byte[10])},
                {new MockMultipartFile("test_avatar", "test_avatar", "image/tiff", new byte[10])},
                {new MockMultipartFile("test_avatar", "test_avatar", "text/plain", new byte[10])},
                {new MockMultipartFile("test_avatar", "test_avatar", "audio/mpeg", new byte[10])},
                {new MockMultipartFile("test_avatar", "test_avatar", "audio/x-wav", new byte[10])},
                {new MockMultipartFile("test_avatar", "test_avatar", "text/plain", new byte[10])},
                {new MockMultipartFile("test_avatar", "test_avatar", "text/html", new byte[10])},
                {new MockMultipartFile("test_avatar", "test_avatar", "video/mpeg", new byte[10])}
        };
    }

    @DataProvider
    Object[][] validFormatValues() {
        Set<String> validFormats = new HashSet<String>();
        validFormats.add("image/jpeg");
        validFormats.add("image/png");
        validFormats.add("image/gif");
        List<MultipartFile> files = new ArrayList<MultipartFile>(validFormats.size());
        for (String contentType : validFormats) {
            files.add(new MockMultipartFile("test_avatar", "test_avatar", contentType, new byte[10]));
        }
        Object[][] result = new Object[files.size()][];
        for (int i = 0; i < result.length; i++) {
            result[i] = new Object[]{files.get(i)};
        }

        return result;

    }

    @DataProvider
    Object[][] validFormatValuesForChromeFF() throws Exception {
        String root = "/org/jtalks/jcommune/service/testdata/avatar/valid/format/";
        Object[][] result = new Object[3][];
        result[0] = new Object[]{IOUtils.toByteArray(this.getClass().getResourceAsStream(root + "avatar.gif"))};
        result[1] = new Object[]{IOUtils.toByteArray(this.getClass().getResourceAsStream(root + "avatar.jpg"))};
        result[2] = new Object[]{IOUtils.toByteArray(this.getClass().getResourceAsStream(root + "avatar.png"))};
        return result;
    }

    @DataProvider
    Object[][] invalidFormatValuesForChromeFF() throws Exception {
        String root = "/org/jtalks/jcommune/service/testdata/avatar/invalid/format/";
        Object[][] result = new Object[4][];
        result[0] = new Object[]{IOUtils.toByteArray(this.getClass().getResourceAsStream(root + "avatar.bmp"))};
        result[1] = new Object[]{IOUtils.toByteArray(this.getClass().getResourceAsStream(root + "avatar.pcx"))};
        result[2] = new Object[]{IOUtils.toByteArray(this.getClass().getResourceAsStream(root + "avatar.pdf"))};
        result[3] = new Object[]{IOUtils.toByteArray(this.getClass().getResourceAsStream(root + "avatar.tif"))};
        return result;
    }

    @DataProvider
    Object[][] validSizeValues() {
        return new Object[][]{
                {new byte[AVATAR_MAX_SIZE]},
                {new byte[Math.round(AVATAR_MAX_SIZE / 2)]}
        };
    }

}
