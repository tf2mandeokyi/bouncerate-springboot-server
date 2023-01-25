import React, { useCallback, useEffect, useState } from 'react'
import { useLocation } from 'react-router-dom'
import { getBounceRate } from '../../api/bouncerate';
import { AdvertisementProduct, getProduct } from '../../api/products';
import { getSetTopBoxesCount, getSetTopBoxesPage, SetTopBox } from '../../api/settopboxes';
import EntityDescriptionTable from '../../components/entity-description';
import EntityTable from '../../components/entity-table';
import LastUpdateDiv from '../../components/last-update';


const AdvertisementProductMenu : React.FC = () => {

    const location = useLocation();
    const params = new URLSearchParams(location.search);
    const productId = parseInt(params.get('id') ?? '-1');

    const [ product, setProduct ] = useState<AdvertisementProduct>();


    const entityToJSX = useCallback(async (setTopBox: SetTopBox) => {
        let bounceRate = await getBounceRate({ productId, setTopBoxId: setTopBox.id });
        return [ <>{ bounceRate }</>, <div className='button darkblue'>수정</div> ]
    }, [ productId ]);


    useEffect(() => {
        (async () => {
            setProduct(await getProduct(productId));
        })();
    }, [ productId, setProduct ]);


    return product ? (
        <>
            <EntityDescriptionTable>
                <tr><td>이름:</td><td>{ product.name }</td></tr>
                <tr><td>데이터베이스 ID:</td><td>{ product.id }</td></tr>
                <tr><td>광고 가능 여부:</td><td>{ product.availability ? '가능' : '불가능' }</td></tr>
                <tr>
                    <td>Bounce rate 점수:</td>
                    <td>
                        { product.bounceRateScore }
                        <LastUpdateDiv date={ product.scoreUpdatedDate } />
                    </td>
                </tr>
            </EntityDescriptionTable>
            <EntityTable<SetTopBox>
                entityNameColumnHead={ [ '셋톱박스 이름', 'Bounce rate' ] }
                getEntityCount={ async () => await getSetTopBoxesCount() }
                getEntitiesPage={ async (e, p) => await getSetTopBoxesPage(e, p) }
                entityToJSX={ entityToJSX }
            />
        </>
    ) : <>Loading...</>
    
}

export default AdvertisementProductMenu;