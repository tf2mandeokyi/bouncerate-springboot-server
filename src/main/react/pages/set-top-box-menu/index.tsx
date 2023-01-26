import React, { useCallback, useEffect, useState } from 'react'
import { useLocation } from 'react-router-dom';
import { getBounceRate } from '../../api/bouncerate';
import { AdvertisementProduct, getProductsCount, getProductsPage } from '../../api/products';
import { getSetTopBox, SetTopBox } from '../../api/settopboxes';
import EntityDescriptionTable from '../../components/entity-description';
import EntityTable from '../../components/entity-table';

const SetTopBoxMenu : React.FC = (props) => {

    const location = useLocation();
    const params = new URLSearchParams(location.search);
    const setTopBoxId = parseInt(params.get('id') ?? '-1');

    const [ setTopBox, setSetTopBox ] = useState<SetTopBox>();


    const getEntityCount = useCallback(async () => await getProductsCount(), []);
    const getEntitiesPage = useCallback(async (e: number, p: number) => await getProductsPage(e, p), []);
    const entityToJSX = useCallback(async (product: AdvertisementProduct) => {
        let bounceRate = await getBounceRate({ productId: product.id, setTopBoxId });
        return [ <>{ bounceRate }</>, <div key={ product.id } className='button darkblue'>수정</div> ]
    }, [ setTopBoxId ]);


    useEffect(() => {
        (async () => {
            setSetTopBox(await getSetTopBox(setTopBoxId));
        })();
    }, [ setTopBoxId ]);

    return setTopBox ? (
        <>
            <EntityDescriptionTable>
                <tr><td>이름:</td><td>{ setTopBox.name }</td></tr>
                <tr><td>데이터베이스 ID:</td><td>{ setTopBox.id }</td></tr>
            </EntityDescriptionTable>
            <EntityTable<AdvertisementProduct>
                entityNameColumnHead={ [ '광고 상품 이름', 'Bounce rate' ] }
                getEntityCount={ getEntityCount }
                getEntitiesPage={ getEntitiesPage }
                entityToJSX={ entityToJSX }
            />
        </>
    ) : <>Loading...</>
}

export default SetTopBoxMenu