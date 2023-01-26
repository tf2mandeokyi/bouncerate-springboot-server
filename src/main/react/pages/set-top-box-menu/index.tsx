import React, { useCallback, useEffect, useState } from 'react'
import { useLocation } from 'react-router-dom';
import { getBounceRate, setBounceRate } from '../../api/bouncerate';
import { AdvertisementProduct, getProductsCount, getProductsPage } from '../../api/products';
import { getSetTopBox, SetTopBox } from '../../api/settopboxes';
import BackToHome from '../../components/back-to-home';
import EntityDescriptionTable from '../../components/entity-description';
import EntityTable, { EntityToJSXFunction } from '../../components/entity-table';

const SetTopBoxMenu : React.FC = () => {

    const location = useLocation();
    const params = new URLSearchParams(location.search);
    const setTopBoxId = parseInt(params.get('id') ?? '-1');

    const [ setTopBox, setSetTopBox ] = useState<SetTopBox>();


    const onBounceRateEditButtonClick = useCallback(async (product: AdvertisementProduct, update: () => void) => {
        let promptInput = prompt('새로운 Bounce rate 값을 입력해주세요.');
        if(!promptInput) return;

        let newBounceRate = parseFloat(promptInput);
        if(isNaN(newBounceRate)) return;

        await setBounceRate({ productId: product.id, setTopBoxId }, newBounceRate);
        update();
    }, [ setTopBoxId ]);


    const getEntityCount = useCallback(async () => await getProductsCount(), []);
    const getEntitiesPage = useCallback(async (e: number, p: number) => await getProductsPage(e, p), []);
    const entityToJSX : EntityToJSXFunction<AdvertisementProduct> = useCallback(async (product, update) => {
        let bounceRate = await getBounceRate({ productId: product.id, setTopBoxId });
        console.log(bounceRate);
        return [ 
            <>{ bounceRate ?? '-' }</>, 
            <div 
                key={ product.id } 
                className='button darkblue'
                onClick={ () => { onBounceRateEditButtonClick(product, update) } }
            >
                수정
            </div>
        ]
    }, [ setTopBoxId, onBounceRateEditButtonClick ]);


    useEffect(() => {
        (async () => {
            setSetTopBox(await getSetTopBox(setTopBoxId));
        })();
    }, [ setTopBoxId ]);

    return setTopBox ? (
        <>
            <BackToHome />
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