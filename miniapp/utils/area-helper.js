const { get } = require('./request');

let cachedAreaList = null;
let cachedNameToCode = null;

function padCode(num, len) {
  return String(num).padStart(len, '0');
}

function transformCityTree(tree) {
  const province_list = {};
  const city_list = {};
  const county_list = {};
  const nameToCode = {};

  tree.forEach((province, pIdx) => {
    const pCode = padCode(pIdx + 1, 2) + '0000';
    province_list[pCode] = province.name;
    nameToCode[`p_${province.name}`] = pCode;

    const cities = province.children || [];
    cities.forEach((city, cIdx) => {
      const cCode = padCode(pIdx + 1, 2) + padCode(cIdx + 1, 2) + '00';
      city_list[cCode] = city.name;
      nameToCode[`c_${province.name}_${city.name}`] = cCode;

      const districts = city.children || [];
      districts.forEach((district, dIdx) => {
        const dCode = padCode(pIdx + 1, 2) + padCode(cIdx + 1, 2) + padCode(dIdx + 1, 2);
        county_list[dCode] = district.name;
        nameToCode[`d_${province.name}_${city.name}_${district.name}`] = dCode;
      });
    });
  });

  return {
    areaList: { province_list, city_list, county_list },
    nameToCode
  };
}

async function getAreaData() {
  if (cachedAreaList) {
    return { areaList: cachedAreaList, nameToCode: cachedNameToCode };
  }
  const res = await get('/api/common/city/tree');
  const tree = res.data || [];
  const { areaList, nameToCode } = transformCityTree(tree);
  cachedAreaList = areaList;
  cachedNameToCode = nameToCode;
  return { areaList, nameToCode };
}

function getCodeByName(nameToCode, province, city, district) {
  return nameToCode[`d_${province}_${city}_${district}`]
    || nameToCode[`c_${province}_${city}`]
    || nameToCode[`p_${province}`]
    || '';
}

module.exports = { getAreaData, getCodeByName };
